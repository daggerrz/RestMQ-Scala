curl -X POST -d "value=foobar" http://localhost:8888/q/test
curl http://localhost:8888/q/test

ab -p post-test.txt -T 'application/x-www-form-urlencoded' -n 100000 -c 50 -k http://localhost:8888/q/test
ab -n 100000 -c 50 -k http://localhost:8888/q/test
