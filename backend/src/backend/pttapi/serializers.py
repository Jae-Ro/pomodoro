# serializers.py
from rest_framework import serializers
from django.core.validators import validate_email
from django.core.exceptions import ValidationError
from .models import *
from django.db import IntegrityError
from rest_framework.validators import UniqueValidator
from rest_framework.exceptions import APIException
from rest_framework import status
from django.utils.encoding import force_text
from django.core.validators import RegexValidator
alpha = RegexValidator(r'^[ _\'\-0-9a-zA-Z]*$', 'Only alphanumeric characters are allowed.')
from .utils import *
from django.utils import dateparse
from .utils import *

class UsersSerializer(serializers.Serializer):
    id = serializers.IntegerField(read_only=True)
    firstName = serializers.CharField(max_length=60,validators=[alpha])
    lastName = serializers.CharField(max_length=60,validators=[alpha])
    # https://github.com/encode/django-rest-framework/issues/1848
    # repeated email will show 400 error not 409
    email = serializers.CharField(max_length=60)

    def create(self, validated_data):
        return Users.objects.create(**validated_data)

    def update(self, instance, validated_data):
        """
        Update and return an existing `Snippet` instance, given the validated data.
        """
        instance.firstName = validated_data.get(
            'firstName', instance.firstName)
        instance.lastName = validated_data.get('lastName', instance.lastName)
        instance.save()
        return instance

    def validate(self, data):
        validate_email(data['email'])
        instance = self.instance
        if instance is not None and data.get('email'):
            originalemail = instance.email
            newemail = data.get('email')
            if newemail is not None and (originalemail != newemail):
                raise ValidationError('Cannot update email')

        #round about way of checking for unique constraint of email when POST
        if not self.instance:
            used_emails = Users.objects.all().values_list('email', flat=True)

            current_email = data.get('email')
            for i in used_emails:
                if str(i) == current_email:
                    raise CustomValidation(
                        'Email already in use', current_email, status_code=status.HTTP_409_CONFLICT)
        return data


class ProjectsSerializer(serializers.Serializer):
    id = serializers.IntegerField(read_only=True)
    projectname = serializers.CharField(max_length=60)
    # adds a user_id field which manages which user this refers to
    user_id = serializers.IntegerField()

    def create(self, validated_data):
        return Projects.objects.create(**validated_data)

    def update(self, instance, validated_data):
        instance.projectname = validated_data.get(
            'projectname', instance.projectname)
        instance.save()
        return instance

    # what is shown on get ===> don't show user_id

    def to_representation(self, instance):
        return {
            'id': instance.id,
            'projectname': instance.projectname
        }

    def validate(self, data):
        pname = data['projectname']
        currentpnames = Projects.objects.filter(user=data['user_id'])
        print(pname, currentpnames)
        for i in currentpnames:
            if str(i) == pname:
                raise CustomValidation(
                    'Duplicate projectname', pname, status_code=status.HTTP_409_CONFLICT)

        return data


class SessionsSerializer(serializers.Serializer):
    id = serializers.IntegerField(read_only=True)
    startTime = serializers.DateTimeField()
    endTime = serializers.DateTimeField()
    counter = serializers.IntegerField()
    # adds a user_id field which manages which user this refers to

    project_id = serializers.IntegerField()


    def to_representation(self, instance):
        return {
            'id': instance.id,
            'startTime': convert_to_required_time_format(str(instance.startTime)),
            'endTime': convert_to_required_time_format(str(instance.endTime)),
            'counter': instance.counter
        }

    def create(self, validated_data):
        return Sessions.objects.create(**validated_data)

    def update(self, instance, validated_data):
        instance.startTime = validated_data.get(
            'startTime', instance.startTime)
        instance.endTime = validated_data.get(
            'endTime', instance.endTime)
        instance.counter = validated_data.get(
            'counter', instance.counter)
        instance.save()
        return instance


    def validate(self, data):

        if int(data['counter']) < 0:
            raise CustomValidation("Counter cannot be negative",data['counter'],status_code= status.HTTP_400_BAD_REQUEST)

        try:
            start = dateparse.parse_datetime(str(data['startTime']))
            end = dateparse.parse_datetime(str(data['endTime']))
            if not start or not end:
                raise CustomValidation("Invalid Time Format",str(end),status_code= status.HTTP_400_BAD_REQUEST)
        except ValueError as v:
                raise CustomValidation("Invalid Time Format",str(end),status_code= status.HTTP_400_BAD_REQUEST)

        if end < start:
            raise CustomValidation("Invalid Time",str(end),status_code= status.HTTP_400_BAD_REQUEST)

        return data