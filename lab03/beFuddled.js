// CSC 369 - Lab 03
// Scott Lam
// slam08@calpoly.edu
// BeFuddled dataset

conn = new Mongo("cslvm31");
db = conn.getDB("slam08");

// Query 1
print("Query 1");
print('db.befuddled.find({"game": 1}).sort({"action.actionNumber": 1})')
cursor = db.befuddled.find({"game": 1}).sort({"action.actionNumber": 1});
while (cursor.hasNext()) {
    printjson(cursor.next());
}


// Query 2
print("Query 2");
print('db.befuddled.find({"game": 1, "action.actionType": "Move"}).sort({"action.actionNumber": 1}).limit(3)');
cursor = db.befuddled.find({"game": 1, "action.actionType": "Move"}).sort({"action.actionNumber": 1}).limit(3);
while (cursor.hasNext()) {
    printjson(cursor.next());
}

// Query 3
print("Query 3");
print('db.befuddled.find({"action.actiontype": "move", "action.location.x": 11, "action.location.y": 12}).sort({"action.points": -1})');
cursor = db.befuddled.find({"action.actiontype": "move", "action.location.x": 11, "action.location.y": 12}).sort({"action.points": -1});
while (cursor.hasNext()) {
    printjson(cursor.next());
}

// Query 4
print("Query 4");
print('db.befuddled.find({"action.actionType": "SpecialMove", "action.pointsAdded": {"$lt": 0}})');
cursor = db.befuddled.find({"action.actionType": "SpecialMove", "action.pointsAdded": {"$lt": 0}});
while (cursor.hasNext()) {
    printjson(cursor.next());
}

// Query 5
print("Query 5");
print('db.befuddled.find({"action.actionType": "GameEnd"}, {"user": 1, "_id": 0})');
cursor = db.befuddled.find({"action.actionType": "GameEnd"}, {"user": 1, "_id": 0});
while (cursor.hasNext()) {
    printjson(cursor.next());
}

// Query 6
print("Query 6");
print('db.befuddled.find({}, {"action.points": 1, "_id": 0}).sort({"action.points": -1}).limit(3)');
cursor = db.befuddled.find({}, {"action.points": 1, "_id": 0}).sort({"action.points": -1}).limit(3);
while (cursor.hasNext()) {
    printjson(cursor.next());
}

// Query 7
print("Query 7");
print('db.befuddled.find({"$or": [{"action.move": "Clear"},{"action.pointsAdded": {"$gte": 10}}]}).sort({"actionType": 1, "action.pointsAdded": -1})');
cursor = db.befuddled.find({"$or": [{"action.move": "Clear"},{"action.pointsAdded": {"$gte": 10}}]}).sort({"actionType": 1, "action.pointsAdded": -1});
while (cursor.hasNext()) {
    printjson(cursor.next());
}

// Query 8
print("Query 8");
print('db.befuddled.find({"action.location.x": {"$lte": 12, "$gte": 8}, "action.location.y": {"$lte": 12, "$gte": 8}, "$or": [{"action.points" : {"$lt": 0}},{"action.points": {"$gt": 10}}]})');
cursor = db.befuddled.find({"action.location.x": {"$lte": 12, "$gte": 8}, "action.location.y": {"$lte": 12, "$gte": 8}, "$or": [{"action.points" : {"$lt": 0}},{"action.points": {"$gt": 10}}]});
while (cursor.hasNext()) {
    printjson(cursor.next());
}

// Query 9
print("Query 9");
print('db.befuddled.find({"action.actionType": "GameStart"}).count()');
cursor = db.befuddled.find({"action.actionType": "GameStart"}).count();
print(cursor);

// Query 10
print("Query 10");
print('db.befuddled.find({"$or": [{"action.pointsAdded": 5},{"action.pointsAdded": 6}]}, {"action.location": 1, "game": 1, "action.actionNumber": 1, "_id": 0}).sort({"game": 1, "action.actionNumber": 1})');
cursor = db.befuddled.find({"$or": [{"action.pointsAdded": 5},{"action.pointsAdded": 6}]}, {"action.location": 1, "game": 1, "action.actionNumber": 1, "_id": 0}).sort({"game": 1, "action.actionNumber": 1});
while (cursor.hasNext()) {
    printjson(cursor.next());
}
