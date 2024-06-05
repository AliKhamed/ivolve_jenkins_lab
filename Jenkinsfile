@Library('Jenkins-Shared-Library')_
pipeline {
    agent any
    
    environment {
        dockerHubCredentialsID	    = 'DockerHub'  		    			// DockerHub credentials ID.
        imageName   		    = 'alikhames/java-app'     			        // DockerHub repo/image name.
	openshiftCredentialsID	    = 'openshift'	    				// KubeConfig credentials ID.   
	nameSpace                   = 'alikhames'
	clusterUrl                  = 'https://api.ocp-training.ivolve-test.com:6443'
    }
    
    stages {       

        stage('Run Unit Test') {
            steps {
                script {
                	
                	runUnitTests
            		
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
