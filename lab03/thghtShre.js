// CSC 369 - Lab 03
// Scott Lam
// slam08@calpoly.edu
// ThghtShre dataset

conn = new Mongo("cslvm31");
db = conn.getDB("slam08");

// Query 1
print("Query 1");
print('db.thghtshre.find({"user": "u100"})');
cursor = db.thghtshre.find({"user": "u100"});
while (cursor.hasNext()) {
    printjson(cursor.next());
}

// Query 2
print("Query 2");
print('db.thghtshre.find().sort({"messageId": -1}).skip(5).limit(6)');
cursor = db.thghtshre.find().sort({"messageId": -1}).skip(5).limit(6);
while (cursor.hasNext()) {
    printjson(cursor.next());
}

// Query 3
print("Query 3");
print('db.thghtshre.find({"recipient": {"$nin": ["all", "self", "subscribers"]}}, {"recipient": 1, "_id": 0}).sort({"recipient": 1})');
cursor = db.thghtshre.find({"recipient": {"$nin": ["all", "self", "subscribers"]}}, {"recipient": 1, "_id": 0}).sort({"recipient": 1});
while (cursor.hasNext()) {
    printjson(cursor.next());
}

// Query 4
print("Query 4");
print('db.thghtshre.find({"status": "protected"}).count()');
cursor = db.thghtshre.find({"status": "protected"}).count();
printjson(cursor);

// Query 5
print("Query 5");
print('db.thghtshre.find({"in-response" : {"$exists": true}}, {"text": 1, "_id": 0})');
cursor = db.thghtshre.find({"in-response" : {"$exists": true}}, {"text": 1, "_id": 0});
while (cursor.hasNext()) {
    printjson(cursor.next());
}

// Query 6
print("Query 6");
print('db.thghtshre.find({"status": "public", "recipient": "self"}).sort({"messageId": -1})');
cursor = db.thghtshre.find({"status": "public", "recipient": "self"}).sort({"messageId": -1});
while (cursor.hasNext()) {
    printjson(cursor.next());
}

// Query 7
print("Query 7");
print('db.thghtshre.find({"status": "public", "recipient": "self", "in-response": {"$exists": true}}, {"text": 1, "_id": 0})');
cursor = db.thghtshre.find({"status": "public", "recipient": "self", "in-response": {"$exists": true}}, {"text": 1, "_id": 0});
while (cursor.hasNext()) {
    printjson(cursor.next());
}

// Query 8
print("Query 8");
print('db.thghtshre.find({}, {"recipient": 1, "text": 1, "_id": 0}).sort({"messageId": -1}).limit(4)');
cursor = db.thghtshre.find({}, {"recipient": 1, "text": 1, "_id": 0}).sort({"messageId": -1}).limit(4);
while (cursor.hasNext()) {
    printjson(cursor.next());
}

// Query 9
print("Query 9");
print('db.thghtshre.find({"status": "private", "recipient": "self"}, {"user": 1, "_id": 0})');
cursor = db.thghtshre.find({"status": "private", "recipient": "self"}, {"user": 1, "_id": 0});
while (cursor.hasNext()) {
    printjson(cursor.next());
}

// Query 10
print("Query 10");
print('db.thghtshre.find({"status": "public", "recipient": "all", "in-response": {"$exists": false}}, {"text": 1, "_id": 0}).sort({"messageId": 1})');
cursor = db.thghtshre.find({"status": "public", "recipient": "all", "in-response": {"$exists": false}}, {"text": 1, "_id": 0}).sort({"messageId": 1});
while (cursor.hasNext()) {
    printjson(cursor.next());
}
