conn = new Mongo("cslvm31");
db = conn.getDB("slam08");
db.thot.drop();
file = cat("thghtShre50.out");
db.thot.insert(JSON.parse(file));
