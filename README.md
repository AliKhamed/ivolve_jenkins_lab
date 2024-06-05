# Jenkins Pipeline Project

This project sets up a Jenkins pipeline to automate the CI/CD process for an application. The pipeline includes stages for testing the application, building a Docker image, pushing the image to Docker Hub, and deploying the application on OpenShift. The pipeline leverages an EC2 instance as a Jenkins slave and uses a shared library hosted on GitHub for reusable pipeline code.

## Prerequisites

1. Jenkins master configured with the following plugins:
   - Git
   - Pipeline
   - Docker
   - OpenShift

2. An EC2 instance configured as a Jenkins slave.
3. Docker ans oc cli installed and configured on the EC2 slave.
4. A GitHub repository containing the shared library.
5. An OpenShift cluster and CLI configured.

## Pipeline Stages

1. **Test**: Run unit tests to ensure the codebase is stable.
2. **Build Docker Image**: Build a Docker image for the application.
3. **Push Docker Image**: Push the Docker image to Docker Hub.
4. **Deploy on OpenShift**: Deploy the application on an OpenShift cluster.

## Setup Instructions

### Configure Jenkins EC2 Slave

1. Launch an EC2 instance.
2. Configure the EC2 instance as a Jenkins build agent.
3. Install Docker on the EC2 instance.
4. Ensure the EC2 instance has access to Docker Hub and OpenShift.

### Set Up GitHub Shared Library

1. Create a shared library in a GitHub repository.
2. Define common pipeline steps (e.g., test, build, push, deploy) in the shared library.

### Create Jenkins Pipeline Job

1. Go to Jenkins dashboard and create a new pipeline job.
2. Configure the job to use the EC2 slave as the build agent.
3. Configure the pipeline script to use the shared library.

## Ansible Playbook for EC2 Setup

Use the following Ansible playbook to install Docker and the OpenShift CLI on the EC2 instance.


