import sys

if __name__ == '__main__':
    if len(sys.argv) != 4:
        print('Not enough arguments')
        print('Usage:')
        print('     python createScript.py <JSON document path> <database name> <collection name>')
        sys.exit(1)

    json = sys.argv[1]
    db = sys.argv[2]
    collection = sys.argv[3]
    with open('insert_%s_%s.js' % (db, collection), 'w+') as f:
        f.write('conn = new Mongo("cslvm31");\n')
        f.write('db = conn.getDB("%s");\n' % db)
        f.write('db.%s.drop();\n' % collection)
        f.write('file = cat("%s");\n' % json)
        f.write('db.%s.insert(JSON.parse(file));\n' % collection)

