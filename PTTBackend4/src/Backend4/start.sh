if [ -f "db.sqlite3" ]; then
    rm db.sqlite3
fi
if [ -f "pttapi/__pycache__" ]; then
    rm -rf pttapi/__pycache__
fi
if [ -f "pttapi/migrations" ]; then
    rm -rf pttapi/migrations
fi
python manage.py makemigrations
python manage.py migrate
python manage.py migrate --run-syncdb
