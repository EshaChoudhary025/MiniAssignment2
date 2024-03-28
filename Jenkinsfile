pipeline {
    agent any
    
    stages {
        stage('Checkout') {
            steps {
               
                checkout scm
            }
        }
        
        stage('Debugging') {
            steps {
              script {
                echo "JAVA_HOME: ${env.JAVA_HOME}"
              }
            }
        }
        
        stage('Build') {
            steps {
               bat 'mvn clean install'
            }
        }
        
        stage('Test') {
            steps {
              script{
                bat 'mvn test'
              }
                
                
            }
        }
    }
    
    post {
      
        
        success {
          
            sh 'echo "Pipeline succeeded!"'
        }
        
        failure {
            
            sh 'echo "Pipeline failed!"'
        }
        
      
    }
}
