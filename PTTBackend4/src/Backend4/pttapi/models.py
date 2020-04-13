from django.db import models
from django.core.validators import RegexValidator
from django.utils import dateparse
from .utils import *

# Create your models here.

class Users(models.Model):
    firstName = models.CharField(max_length=60)
    lastName = models.CharField(max_length=60)
    email = models.CharField(max_length=60, unique=True)

    def __str__(self):
        return self.firstName + " " + self.lastName


class Projects(models.Model):
    user = models.ForeignKey(Users, on_delete=models.CASCADE)
    projectname = models.CharField(max_length=60)

    def __str__(self):
        return self.projectname


class Sessions(models.Model):
    startTime = models.CharField(max_length=60)
    endTime = models.CharField(max_length=60)
    counter = models.IntegerField()
    project = models.ForeignKey(Projects, on_delete=models.CASCADE)
    def __str__(self):
        return str(self.startTime) + ", " + str(self.endTime) + ", " + str(self.counter)

    def get_start(self):
        return convert_to_required_time_format(self.startTime)

    def get_end(self):
        return convert_to_required_time_format(self.endTime)

    def get_counter(self):
        return self.counter