pipeline {
    agent any

    parameters {
        choice (choices: ['Baseline', 'APIS'],  //, "APIS", "Full"
                description: 'Type of scan that is going to perform inside the container',
                name: 'SCAN_TYPE')

        string (defaultValue: 'http://host.docker.internal:5000',
                description: 'Target URL to scan',
                name: 'TARGET')

        string (defaultValue: 'http://host.docker.internal:5001',
                description: 'Second target URL to scan',
                name: 'TARGET2')

        booleanParam (defaultValue: true,
                description: 'Parameter to know if wanna generate report.',
                name: 'GENERATE_REPORT')
    }

    stages{
        stage('producer build'){
            agent{
                docker{
                    image 'kernetix/producer:latest'           
                }
            }

            // when{
            //     changeset "producer/*"
            // }

            steps{
                echo 'Compiling producer app'
                dir('producer'){
                    sh 'mvn compile'
                }
            }
        }

        stage('producer test'){
            agent{
                docker{
                    image 'kernetix/producer:latest'          
//                    args '-v $HOME/.m2:/root/.m2'
                }
            }

            // when{
            //     changeset "producer/*"
            // }

            steps{
                echo 'Running Unit Tets on producer app'
                dir('producer'){
                    sh 'mvn test'
                }
            }
        }

        stage('producer package'){
            agent{
                docker{
                    image 'kernetix/producer:latest'             
                }
            }

            // when{
            //     changeset "producer/*"
            // }
            
            steps{
                echo 'Packaging producer app'
                dir('producer'){
                    sh 'mvn clean package spring-boot:repackage -DskipTests'
                }
            }
        }

        stage('Producer docker-package'){          
            agent any          
            
            // when{            
            //     changeset "producer/*"                  
            // }          

            steps{            
                echo 'Packaging producer app with docker: version and latest'            
                script{              
                    docker.withRegistry('https://index.docker.io/v1/', 'dockerlogin') 
                        {
                        def producerImage = docker.build("kernetix/producer:latest", "./producer")                                              
                            producerImage.push()              
                        }            
                }          
            }      
        }  

        stage('consumer build'){
            agent{
                docker{
                    image 'kernetix/consumer:latest'           
                }
            }

            // when{
            //     changeset "consumer/*"
            // }

            steps{
                echo 'Compiling consumer app'
                dir('consumer'){
                    sh 'mvn compile'
                }
            }
        }

        stage('consumer test'){
            agent{
                docker{
                    image 'kernetix/consumer:latest'          
                }
            }

            // when{
            //     changeset "consumer/*"
            // }

            steps{
                echo 'Running Unit Tests on consumer app'
                dir('consumer'){
                    sh 'mvn test'
                }
            }
        }

        stage('consumer package'){
            agent{
                docker{
                    image 'kernetix/consumer:latest'             
                }
            }

            // when{
            //     changeset "consumer/*"
            // }
            
            steps{
                echo 'Packaging consumer app'
                dir('consumer'){
                    sh 'mvn clean package spring-boot:repackage -DskipTests'
                }
            }
        }

        stage('consumer docker-package'){          
            agent any          
            
            // when{            
            //     changeset "consumer/*"                 
            // }          

            steps{            
                echo 'Packaging consumer app with docker: version and latest'            
                script{              
                    docker.withRegistry('https://index.docker.io/v1/', 'dockerlogin') 
                        {
                        def consumerImage = docker.build("kernetix/consumer:latest", "./consumer")                                                 
                            consumerImage.push()              
                        }            
                }          
            }      
        } 

        // stage('Sonarqube') {
        //     agent any
        
        //     tools {
        //         jdk 'JDK11'
        //     }
            
        //     environment{
        //         sonarpath = tool 'Sonar'
        //     }
        
        //     steps {
        //         echo 'Running Sonarqube Analysis..'
        //         withSonarQubeEnv('Sonarcube-Voteapp') {
        //         sh '${sonarpath}/bin/sonar-scanner'
        //         }
        //     }
        // }

        // stage("Quality Gate") {
            
        //     when{
        //         branch 'master'
        //     }
            
        //     steps {
        //         // Parameter indicates whether to set pipeline to UNSTABLE if Quality Gate fails
        //         // true = set pipeline to UNSTABLE, false = don't
        //         timeout(time: 1, unit: 'HOURS') {
        //             waitForQualityGate abortPipeline: true
        //         }
        //     }
        // }

        // stage('e2e test'){
        //     agent any
        
        //     when{
        //         branch 'master'
        //     }
        
        //     steps{
        //         echo 'Running E2E test on the app'
        //         sh './e2e.sh'
        //     }
        // }

        stage('Deploy'){
            agent any
            
            steps{
                echo 'Deploy instavote app with docker compose'
                sh 'docker network create jenkins'  
                sh 'docker compose up -d'
                sleep time: 60, unit: 'SECONDS'
            }
        }

        stage('Pipeline Info') {
            agent any

            steps {
                script {
                    echo "<--Parameter Initialization-->"
                    echo """
                    The current parameters are:
                        Scan Type: ${params.SCAN_TYPE}
                        Target: ${params.TARGET}
                        Target2: ${params.TARGET2}
                        Generate report: ${params.GENERATE_REPORT}
                    """
                }
            }
        }

        stage('Setting up OWASP ZAP docker container') {
            agent any

            steps {
                script {
                        echo "Pulling up last OWASP ZAP container --> Start"
                        sh 'docker pull owasp/zap2docker-stable'
                        echo "Pulling up last VMS container --> End"
                        echo "Starting container --> Start"
                        sh """
                        docker run -p 9090:8090 -dt --name owasp \
                        --net jenkins \
                        owasp/zap2docker-stable \
                        /bin/bash
                        """
                }
            }
        }

        stage('Prepare work directory') {
            agent any

            when {
                environment name : 'GENERATE_REPORT', value: 'true'
            }

            steps {
                script {
                        sh """
                            docker exec owasp \
                            mkdir /zap/wrk
                        """
                    }
                }
        }

        stage('Scanning target on owasp container') {
            agent any

            steps {
                script {
                    scan_type = "${params.SCAN_TYPE}"
                    target = "${params.TARGET}"
                    target2 = "${params.TARGET2}"
                    echo "----> scan_type: $scan_type"

                    if(scan_type == "Baseline"){
                        sh """
                            docker exec owasp \
                            zap-baseline.py \
                            -t $target \
                            -x report.xml \
                            -I
                        """
                        sh """
                            docker exec owasp \
                            zap-baseline.py \
                            -t $target2 \
                            -x report2.xml \
                            -I
                        """
                    }
                    else if(scan_type == "APIS"){
                        sh """
                            docker exec owasp \
                            zap-api-scan.py \
                            -t $target \
                            -x report.xml \
                            -I
                        """
                        sh """
                            docker exec owasp \
                            zap-api-scan.py \
                            -t $target2 \
                            -x report2.xml \
                            -I
                        """
                    }
                    else if(scan_type == "Full"){
                        sh """
                            docker exec owasp \
                            zap-full-scan.py \
                            -t $target \
                            //-x report_test.xml
                            -I
                        """
                        sh """
                            docker exec owasp \
                            zap-full-scan.py \
                            -t $target2 \
                            //-x report2.xml
                            -I
                        """
                        //-x report-$(date +%d-%b-%Y).xml
                    }
                }
            }
        }

        stage('Copy Report to Workspace'){
            agent any

            steps {
                script {
                    sh '''
                        docker cp owasp:/zap/wrk/report.xml "${WORKSPACE}/report.xml"
                    '''
                    sh '''
                        docker cp owasp:/zap/wrk/report.xml "${WORKSPACE}/report2.xml"
                    '''
                    }
            }
        }
    }

    post{
        always{
            script {
                echo 'Building multibranch pipeline for worker is completed' 
                echo "Removing container..."
                sh '''
                    docker stop owasp
                '''
                sh '''
                    docker rm owasp
                '''
                sh '''
                    docker compose down
                '''                
            }             
        }

        failure{
            slackSend (channel: "continuous-delivery-notification", message: "Build Failed - ${env.JOB_NAME} ${env.BUILD_NUMBER} (<${env.BUILD_URL}|Open>)")
        }

        success{
            jacoco(
                execPattern: '**/**.exec',
                classPattern: '**/classes',
                sourcePattern: '**/src/main/java',
                sourceInclusionPattern: '**/*.java,**/*.groovy,**/*.kt,**/*.kts'
            )

            slackSend (channel: "continuous-delivery-notification", message: "Build Succeeded - ${env.JOB_NAME} ${env.BUILD_NUMBER} (<${env.BUILD_URL}|Open>)")
        }
    }
}