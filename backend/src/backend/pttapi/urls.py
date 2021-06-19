from django.urls import include, path
from . import views

urlpatterns = [
    path('users', views.UserViewSet.as_view()),
    path('users/', views.UserViewSet.as_view()),
    path('users/<int:u_pk>', views.UserIDViewSet.as_view()),
    path('users/<int:u_pk>/', views.UserIDViewSet.as_view()),
    path('users/<int:u_pk>/projects/', views.ProjectViewSet.as_view()),
    path('users/<int:u_pk>/projects/<int:p_pk>/', views.ProjectIDViewSet.as_view()),
    path('users/<int:u_pk>/projects', views.ProjectViewSet.as_view()),
    path('users/<int:u_pk>/projects/<int:p_pk>', views.ProjectIDViewSet.as_view()),
    path('users/<int:u_pk>/projects/<int:p_pk>/report', views.ReportViewSet.as_view()),
    path('users/<int:u_pk>/projects/<int:p_pk>/report/', views.ReportViewSet.as_view()),
    path('users/<int:u_pk>/projects/<int:p_pk>/sessions', views.SessionsViewSet.as_view()),
    path('users/<int:u_pk>/projects/<int:p_pk>/sessions/', views.SessionsViewSet.as_view()),
    path('users/<int:u_pk>/projects/<int:p_pk>/sessions/<int:s_pk>', views.SessionIDViewSet.as_view()),
    path('users/<int:u_pk>/projects/<int:p_pk>/sessions/<int:s_pk>/', views.SessionIDViewSet.as_view())
]