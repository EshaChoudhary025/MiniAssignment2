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
               sh 'mvn clean install'
            }
        }
        
        stage('Test') {
            steps {
              script{
                sh 'mvn test'
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
