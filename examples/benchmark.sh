curl -X POST -d "{message:'hello'}" -v http://localhost:8888/q/test
curl -v http://localhost:8888/q/test

ab -p post-test.txt -T 'application/x-www-form-urlencoded' -n 100000 -c 50 -k http://127.0.0.1:8888/q/test
ab -n 100000 -c 50 -k http://127.0.0.1:8888/q/test
