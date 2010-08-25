# Not thread safe yet. :)
ab -p post-test.txt -T 'application/x-www-form-urlencoded' -n 100000 -c 1 -k http://localhost:8888/q/test
ab -n 100000 -c 1 -k http://localhost:8888/q/test
