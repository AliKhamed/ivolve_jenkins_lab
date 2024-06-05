# Jenkins Pipeline Project

This project sets up a Jenkins pipeline to automate the CI/CD process for an application. The pipeline includes stages for testing the application, building a Docker image, pushing the image to Docker Hub, and deploying the application on OpenShift. The pipeline leverages an EC2 instance as a Jenkins slave and uses a shared library hosted on GitHub for reusable pipeline code.

## Prerequisites

1. Jenkins master configured with the following plugins:
   - Git
   - Pipeline
   - Docker
   - OpenShift

2. An EC2 instance configured as a Jenkins slave.
3. OpenJDK, Docker and oc cli installed and configured on the EC2 slave using Ansible Playbook.
4. A GitHub repository containing the shared library. 
5. An OpenShift cluster and CLI configured.

## Pipeline Stages

1. **Test**: Run unit tests to ensure the codebase is stable.
2. **Build Docker Image**: Build a Docker image for the application.
3. **Edit new image in deployment.yaml file** Edit new image to push on docker hub
4. **Push Docker Image**: Push the Docker image to Docker Hub.
5. **Deploy on OpenShift**: Deploy the application on an OpenShift cluster.

## Setup Instructions

### Configure Jenkins EC2 Slave

1. Launch an EC2 instance.
   
   ![](https://github.com/AliKhamed/ivolve_jenkins_lab/blob/master/screenshots/ec2.png)
   
2. Configure the EC2 instance as a Jenkins build agent.
   
   ![](https://github.com/AliKhamed/ivolve_jenkins_lab/blob/master/screenshots/node1.png)
   
   ![](https://github.com/AliKhamed/ivolve_jenkins_lab/blob/master/screenshots/node2.png)
   
3. Install OpenJDK, Docker and oc cli on the EC2 instance Using Ansible Playbook.
   
   #### In this repo there is Ansible folder download it and install playbook roles
   
   ![](https://github.com/AliKhamed/ivolve_jenkins_lab/blob/master/screenshots/ansible.png)

### Set Up GitHub Shared Library

[GitHub Shared Library link](https://github.com/AliKhamed/shared_library_oc)

1. Create a shared library in a GitHub repository.
   
   ![](https://github.com/AliKhamed/ivolve_jenkins_lab/blob/master/screenshots/shared2.png)
   
2. Define common pipeline steps (e.g., test, build, push, deploy) in the shared library As a groovy function.
   
  ![](https://github.com/AliKhamed/ivolve_jenkins_lab/blob/master/screenshots/shared3.png)

#### runUnitTests function
   ```
	   #!/usr/bin/env groovy
	   def call() {
		echo "Running Unit Test..."
		sh './gradlew clean test'	
	      }
   ```

#### buildandPushDockerImage function
   ```
	    #!usr/bin/env groovy
	    def call(String dockerHubCredentialsID, String imageName) {
	
		// Log in to DockerHub 
		withCredentials([usernamePassword(credentialsId: "${dockerHubCredentialsID}", usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
			sh "docker login -u ${USERNAME} -p ${PASSWORD}"
	        }
	        
	        // Build and push Docker image
	        echo "Building and Pushing Docker image..."
	        sh "docker build -t ${imageName}:${BUILD_NUMBER} ."
	        sh "docker push ${imageName}:${BUILD_NUMBER}"	 
	       }
   ```

#### editNewImage function
   ```
	#!/usr/bin/env groovy

       def call(String imageName) {
    
       // Edit deployment.yml with new Docker Hub image
       sh "sed -i 's|image:.*|image: ${imageName}:${BUILD_NUMBER}|g' deployment.yml"

        }
   ```

#### deployOnOc function
   ```
	 #!/usr/bin/env groovy
	
	 def call(String openshiftCredentialsID, String nameSpace, String clusterUrl) {
	
	    
	    // Login to OpenShift using the service account token
	    withCredentials([string(credentialsId: openshiftCredentialsID, variable: 'OC_TOKEN')]) {
	        sh "oc login --token=$OC_TOKEN --server=$clusterUrl --insecure-skip-tls-verify"
	    }
	
	    // Apply the updated deployment.yaml to the OpenShift cluster
	    sh "oc apply -f . --namespace=${nameSpace}"
	}
   ```
   
4. And configure it in Jenkins
   
   ![](https://github.com/AliKhamed/ivolve_jenkins_lab/blob/master/screenshots/shared1.png)

### Set Up Jenkins Credentials 

1. Create The github, dockerhub, oc-token As.
   
   ![](https://github.com/AliKhamed/ivolve_jenkins_lab/blob/master/screenshots/cred1.png)
   
2. Define common pipeline steps (e.g., test, build, push, deploy) in the shared library.
   
3. And oc-token in Jenkins As
   #### get token from openshift cluster by
   
   ![](https://github.com/AliKhamed/ivolve_jenkins_lab/blob/master/screenshots/gettoken.png)
   
   #### And add it in
   
   ![](https://github.com/AliKhamed/ivolve_jenkins_lab/blob/master/screenshots/cred2.png)

### Create Jenkins Pipeline Job

1. Go to Jenkins dashboard and create a new pipeline job and write.
   
   ![](https://github.com/AliKhamed/ivolve_jenkins_lab/blob/master/screenshots/create_pip.png)
   
   #### And Configure your pipline as following
   
   ![](https://github.com/AliKhamed/ivolve_jenkins_lab/blob/master/screenshots/pipconfig1.png)
   
   ![](https://github.com/AliKhamed/ivolve_jenkins_lab/blob/master/screenshots/pipconfig2.png)
   
2. Configure the job to use the EC2 slave as the build agent.
   
   ```
   pipeline {
    agent { 
        // Specifies a label to select an available agent
         node { 
             label 'jenkins-slave'
         }
    }
   
   ```
   
3. Configure the pipeline script to use the shared library by adding the following line in the first line in your Jenkinsfile Pipeline.
   
   ```
   @Library('Jenkins-Shared-Library')_
   
   ```

4. The final Jenkinsfile or pipeline contents
   
```
   
@Library('Jenkins-Shared-Library')_
pipeline {
    agent { 
        // Specifies a label to select an available agent
         node { 
             label 'jenkins-slave'
         }
    }
    
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
```

### Build Pipeline
#### Results 
   ![](https://github.com/AliKhamed/ivolve_jenkins_lab/blob/master/screenshots/pip1.png)
   ![](https://github.com/AliKhamed/ivolve_jenkins_lab/blob/master/screenshots/pip2.png)

### Check the openshift cluster 
#### Login into your cluster and run 
   ![](https://github.com/AliKhamed/ivolve_jenkins_lab/blob/master/screenshots/oc-check.png)

### Check your Application 
#### By run

```
oc get route route-name -n namespace
```

#### And add route link into your browser

![](https://github.com/AliKhamed/ivolve_jenkins_lab/blob/master/screenshots/approute.png)

#### If your App is running you will see this result if not check your configurations.

