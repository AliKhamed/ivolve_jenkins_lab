@Library('Jenkins-Shared-Library')_
pipeline {
    agent any
	// { 
 //        // Specifies a label to select an available agent
 //         node { 
 //             label 'jenkins-slave'
 //         }
 //    }
    
    environment {
        dockerHubCredentialsID	    = 'DockerHub'  		    			// DockerHub credentials ID.
        imageName   		    = 'alikhames/java-app'     			        // DockerHub repo/image name.
	openshiftCredentialsID	    = 'openshift'	    				// KubeConfig credentials ID.   
	nameSpace                   = 'alikhames'
	clusterUrl                  = 'https://api.ocp-training.ivolve-test.com:6443'
	SONAR_PROJECT_KEY           = 'my-sonarqube-demo'
    }
    
    stages {       

        stage('Run Unit Test') {
            steps {
                script {
                	
                	runUnitTests
            		
        	}
    	    }
	}
	stage('Build') {
            steps {
                sh 'chmod +x ./gradlew'
                sh './gradlew clean build'
            }
        }
	stage('SonarQube Analysis') {
            steps {
                withCredentials([string(credentialsId: 'sonar-token', variable: 'SONAR_TOKEN')]) {
                    sh """
                        ./gradlew sonar \
                        -Dsonar.projectKey=${SONAR_PROJECT_KEY} \
                        -Dsonar.host.url=http://192.168.49.1:9000/ \
                        -Dsonar.token=${SONAR_TOKEN} \
                        -Dsonar.scm.provider=git \
                        -Dsonar.java.binaries=build/classes
                    """
                }
            }
        }
       
        stage('Build and Push Docker Image') {
            steps {
                script {
                 	
                 	buildandPushDockerImage("${dockerHubCredentialsID}", "${imageName}")
                        
                    	
                }
            }
        }
	stage('Edit new image in deployment.yaml file') {
            steps {
                script { 
                	dir('oc') {
				        editNewImage("${imageName}")
			}
                }
            }
        }

        stage('Deploy on OpenShift Cluster') {
            steps {
                script { 
			dir('oc') {
                        
				deployOnOc("${openshiftCredentialsID}", "${nameSpace}", "${clusterUrl}")
			}
                }
            }
        }
    }

    post {
        success {
            echo "${JOB_NAME}-${BUILD_NUMBER} pipeline succeeded"
        }
        failure {
            echo "${JOB_NAME}-${BUILD_NUMBER} pipeline failed"
        }
    }
}
