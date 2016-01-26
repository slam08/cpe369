conn = new Mongo("cslvm31");
db = conn.getDB("slam08");
db.fudd.drop();
file = cat("beFuddled50.out");
db.fudd.insert(JSON.parse(file));
