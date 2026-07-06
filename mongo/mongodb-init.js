#!/bin/bash

echo "Waiting Mongo..."

until mongosh --host mongodb-url-spring --eval "db.adminCommand('ping')" >/dev/null 2>&1; do
sleep 2
done

echo "Init replica set..."

mongosh --host mongodb-url-spring --eval '
rs.initiate({
    _id: "rs0",
    members: [
        { _id: 0, host: "mongodb-url-spring:27017" }
    ]
})
'
