# Prime calculator
The application provides a way to calculate the N<sup>th</sup> prime in a distributed system.
It achieves this using a horizontally scaling backend service that stores the calculations in
a shared database. Client requests go to the loadbalancer that provides distribution between 
the backend instances. The algorithm is not very effective in actually calculating the values,
and it does not leverage the full potential of a distributed system. The project is mainly a
tech demo of a horizontally-scaled, cotainerized service sharing data between instances.

## Building the application
There are different ways to build and run the application, the simplest being docker-compose.
Running
```bash
docker-compose up --scale primes=3
```
will:
 - download and run the required images (nginx and mongodb)
 - build the prime calculator docker image and spin up three replicas
 - start nginx at port 5000, delegating requests to the backend
### Building the docker image on its own
If you wish to build the docker image separately for any reason, use
```bash
docker build -t primes .
```
Note that it uses the docker spring profile, requiring mongodb to be running on the mongodb host.
### Deploying to a Kubernetes cluster
If you wish to deploy the application to a Kubernetes cluster, first you have to push the image
to a registry reachable from inside the cluster. Tag image with:
```bash
docker tag primes <your-docker-registry>:primes
```
And push it to the registry:
```bash
docker push <your-docker-registry>:primes
```
Update the `deployment.yml` file, replacing <your-docker-registry> with the actual value, and apply
the deployment:
```bash
kubectl apply -f deployment.yml
```
It can be scaled with
```bash
kubectl scale deployment primes --replicas=10
```
The service needs mongodb to be available, and depending on the routing in the cluster, the service 
needs a `LoadBalancer` or `ClusterIP` with an Ingress Controller set.
The following helm charts can be deployed and configured, for example:
 - [mongodb](https://github.com/helm/charts/tree/master/stable/mongodb)
 - [nginx-ingress](https://github.com/helm/charts/tree/master/stable/nginx-ingress)

## Using the application
The UI is available on the root (`http://localhost:5000` if `docker-compose` was used) that calls the
api at `http://localhost:5000/api/{number}`
