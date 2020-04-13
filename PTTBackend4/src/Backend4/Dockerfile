FROM python:3.7

ENV PYTHONUNBUFFERED 1

RUN mkdir /code
WORKDIR /code

RUN pip install pip -U

ADD requirements.txt /code/

RUN pip install -r requirements.txt



EXPOSE 8000
ADD . /code/
COPY start.sh /code
RUN chmod +x start.sh
RUN sh start.sh


#STOPSIGNAL SIGTERM
EXPOSE 8000
CMD [ "python", "manage.py runserver 0.0.0.0:8000" ]




