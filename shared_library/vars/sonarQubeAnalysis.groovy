#!/usr/bin/env groovy

def call(){ 
	withSonarQubeEnv() { 
		    sh 'chmod +x gradlew'
        	sh "./gradlew sonar" 
	}
}

// def call(String sonarTokenCredentialsID, String SONAR_PROJECT_KEY, String sonarqubeUrl) {

//     withCredentials([string(credentialsId: 'sonarTokenCredentialsID', variable: 'SONAR_TOKEN')]) {
//                         sh  """
//                             ./gradlew sonar \
//                             -Dsonar.projectKey=${SONAR_PROJECT_KEY} \
//                             -Dsonar.host.url=${sonarqubeUrl} \
//                             -Dsonar.token=${SONAR_TOKEN} \
//                             -Dsonar.scm.provider=git \
//                             -Dsonar.java.binaries=build/classes
//                             """
//           }
// }
