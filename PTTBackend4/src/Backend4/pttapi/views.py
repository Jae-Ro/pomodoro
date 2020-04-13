from django.shortcuts import render
from rest_framework import viewsets
import re

from .serializers import *
from .models import *
from rest_framework.response import Response
from django.http import Http404, HttpResponseBadRequest
from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework import status
from rest_framework_extensions.mixins import NestedViewSetMixin
from urllib.parse import parse_qs
import dateutil.parser
from rest_framework import status
from django.http import JsonResponse
from django.utils import dateparse

class UserViewSet(APIView):
    def get(self, request, format=None):
        users = Users.objects.all()
        serializer = UsersSerializer(users, many=True)
        return Response(serializer.data)

    def post(self, request, format=None):

        serializer = UsersSerializer(data=request.data)
        # print(serializer.validate_empty_values())
        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data, status=status.HTTP_201_CREATED)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)


class UserIDViewSet(NestedViewSetMixin, APIView):



    def get_object(self, u_pk):
        try:
            return Users.objects.get(id=u_pk)
        except Users.DoesNotExist:
            raise Http404

    def get(self, request, u_pk, format=None):
        users = self.get_object(u_pk)
        serializer = UsersSerializer(users)
        return Response(serializer.data)

    def put(self, request, u_pk, format=None):
        users = self.get_object(u_pk)
        serializer = UsersSerializer(users, data=request.data)
        if serializer.is_valid(raise_exception = True):
            serializer.save()
            return Response(serializer.data)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

    def delete(self, request, u_pk, format=None):
        users = self.get_object(u_pk)
        serializer = UsersSerializer(users)
        deleted_user = serializer.data
        users.delete()
        return Response(deleted_user)


class ProjectViewSet(NestedViewSetMixin, APIView):
    def get(self, request, u_pk, format=None):
        try:
            user = Users.objects.get(id=u_pk)
        except Users.DoesNotExist:
            raise Http404
        projects = Projects.objects.filter(user_id=u_pk)
        serializer = ProjectsSerializer(projects, many=True)
        return Response(serializer.data)

    def post(self, request, u_pk, format=None):
        try:
            user = Users.objects.get(id=u_pk)
        except Users.DoesNotExist:
            raise Http404
            projects = Projects.objects.filter(user_id=u_pk)
            serializer = ProjectsSerializer(projects, many=True)
            return Response(serializer.data)
        data = request.data
        data["user_id"] = u_pk
        serializer = ProjectsSerializer(data=data)
        if serializer.is_valid(raise_exception=True):
            serializer.save()
            return Response(serializer.data, status=status.HTTP_201_CREATED)
        return Response(serializer.errors, status=status.HTTP_201_CREATED)


class ProjectIDViewSet(NestedViewSetMixin, APIView):
    def get_object(self, u_pk, p_pk):
        try:
            return Projects.objects.get(id=p_pk, user_id=u_pk)
        except Projects.DoesNotExist:
            raise Http404

    def get(self, request, u_pk, p_pk):
        projects = self.get_object(u_pk, p_pk)
        serializer = ProjectsSerializer(projects)
        return Response(serializer.data)

    def put(self, request, u_pk, p_pk, format=None):
        projects = self.get_object(u_pk, p_pk)
        data = request.data
        data["user_id"] = u_pk
        serializer = ProjectsSerializer(projects, data=data)
        if serializer.is_valid(raise_exception=True):
            serializer.save()
            return Response(serializer.data,status=status.HTTP_200_OK)
        else:
            return Response(serializer.errors, status=status.HTTP_409_CONFLICT)

    def delete(self, request, u_pk, p_pk, format=None):
        projects = self.get_object(u_pk, p_pk)
        serializer = ProjectsSerializer(projects)
        deleted_project = serializer.data
        projects.delete()
        return Response(deleted_project)


class SessionsViewSet(NestedViewSetMixin, APIView):
    def get(self, request,u_pk,p_pk, format=None):
        try:
            projects = Projects.objects.get(id=p_pk,user_id=u_pk)
        except Projects.DoesNotExist:
            raise Http404
        sessions = Sessions.objects.filter(project_id=p_pk)

        serializer = SessionsSerializer(sessions, many=True)
        return Response(serializer.data)

    def post(self, request, u_pk, p_pk, format=None):
        try:
            projects = Projects.objects.get(id=p_pk,user_id=u_pk)
        except Projects.DoesNotExist:
            raise Http404

        data = request.data
        data["user_id"] = u_pk
        data['project_id'] = p_pk
        serializer = SessionsSerializer(data=data)
        if serializer.is_valid(raise_exception=True):
            serializer.save()
            return Response(serializer.data, status=status.HTTP_201_CREATED)




class SessionIDViewSet(NestedViewSetMixin, APIView):
    def put(self, request, u_pk, p_pk, s_pk,format=None):


        try:
            projects = Projects.objects.get(id=p_pk,user_id=u_pk)
            sessions = Sessions.objects.get(id=s_pk,project_id=p_pk)
        except Sessions.DoesNotExist:
            raise Http404
        except Projects.DoesNotExist:
            raise Http404

        data = request.data
        #data["user_id"] = u_pk
        data['project_id'] = p_pk
        serializer = SessionsSerializer(sessions,data=data)
        if serializer.is_valid(raise_exception=True):
            serializer.save()
            return Response(serializer.data, status=status.HTTP_200_OK)


class ReportViewSet(NestedViewSetMixin, APIView):
        def get(self, request, u_pk, p_pk, format=None):
            try:
                projects = Projects.objects.get(id=p_pk,user_id=u_pk)
            except Projects.DoesNotExist:
                raise Http404
            sessions = Sessions.objects.filter(project_id=p_pk)

            try:
                r = re.compile('\d{4}\-\d{2}\-\d{2}T\d{2}\:\d{2}Z')
                if r.match(request.GET['from']) is not None and r.match(request.GET['to']):
                    query_from = dateutil.parser.parse(request.GET['from'])
                    query_to = dateutil.parser.parse(request.GET['to'])
                else:
                    return Response(status=status.HTTP_400_BAD_REQUEST)
            except:
                return Response(status=status.HTTP_400_BAD_REQUEST)

            includeCompletedPomodoros = False
            includeTotalHoursWorkedOnProject = False
            if "includeCompletedPomodoros" in request.GET:
                try:
                    includeCompletedPomodoros = str2bool(request.GET['includeCompletedPomodoros'])
                except:
                    return Response(status=status.HTTP_400_BAD_REQUEST)
            if "includeTotalHoursWorkedOnProject" in request.GET:
                try:
                    includeTotalHoursWorkedOnProject = str2bool(request.GET['includeTotalHoursWorkedOnProject'])
                except:
                    return Response(status=status.HTTP_400_BAD_REQUEST)
            #TODO: Add logic to include pomedoroes according to time and fill completedPomodoros variable
            report = {
                "sessions":[]
            }
            if includeCompletedPomodoros:
                report["completedPomodoros"] = 0
            if includeTotalHoursWorkedOnProject:
                report["totalHoursWorkedOnProject"] = 0.0

            for session in sessions:
                session_start = dateutil.parser.parse(session.get_start())
                session_end = dateutil.parser.parse(session.get_end())
                timediff = round((session_end - session_start).total_seconds()/3600, 2)
                if (session_start >= query_from and session_end <= query_to) or (session_start <= query_from and session_end <= query_to and session_end >=query_from) or (session_start >= query_from and session_start <= query_to and session_end >= query_to):
                    if includeCompletedPomodoros:
                        report["completedPomodoros"] += session.get_counter()
                    report["sessions"].append(
                        {
                            "startingTime": session.get_start(),
                            "endingTime": session.get_end(),
                            "hoursWorked": timediff
                        }
                    )
                if includeTotalHoursWorkedOnProject:
                    report["totalHoursWorkedOnProject"] += timediff

            if includeTotalHoursWorkedOnProject:
                report["totalHoursWorkedOnProject"] = round(report["totalHoursWorkedOnProject"], 2)

            return JsonResponse(report)



def str2bool(v):
    if v.lower() in ("yes", "true", "t", "1", "no", "false", "f", "0"):
        return v.lower() in ("yes", "true", "t", "1")
    raise Exception("Incorrect Value For True/False")