/**
 * This pipeline will deploy to Kubernetes
 */

library identifier: 'fabric8-pipeline-library@v2.2.311', retriever: modernSCM(
  github(repoOwner: 'fabric8io', repository: 'fabric8-pipeline-library')
)

podTemplate(label: 'deploy', containers: [
  containerTemplate(name: 'jnlp', image: 'jenkinsci/jnlp-slave:latest'),
  ]) {

  def url = "https://gist.githubusercontent.com/carlossg/e6d847e527ffc65af4a286681fc7c127/raw/b446c13e78633d37a7b21cdf023ad584131b130b"

  stage('deployment') {
    node('deploy') {
      sh "wget ${url}/kubernetes-hello-world-service.yaml"
      sh "wget ${url}/kubernetes-hello-world-v1.yaml"
      kubernetesApply(file: readFile('kubernetes-hello-world-service.yaml'), environment: 'kubernetes-plugin')
      kubernetesApply(file: readFile('kubernetes-hello-world-v1.yaml'), environment: 'kubernetes-plugin')
    }
  }

  stage('upgrade') {
    timeout(time:1, unit:'DAYS') {
      input message:'Approve upgrade?'
    }
    node('deploy') {
      sh "wget ${url}/kubernetes-hello-world-v2.yaml"
      kubernetesApply(file: readFile('kubernetes-hello-world-v2.yaml'), environment: 'kubernetes-plugin')
    }
  }
}
