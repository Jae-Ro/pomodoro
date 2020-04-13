rm db.sqlite3
rm -rf pttapi/__pycache__
rm -rf pttapi/migrations
python manage.py makemigrations
python manage.py migrate
python manage.py migrate --run-syncdb