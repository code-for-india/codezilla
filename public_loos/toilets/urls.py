from django.conf.urls import patterns, include, url
from .views import *
from django.contrib.auth.decorators import user_passes_test
from django.shortcuts import redirect
login_forbidden =  user_passes_test(lambda u: u.is_anonymous(), '/')
urlpatterns = [
#    url(r'^signup/$', login_forbidden(views.userSignup), name='user_signup'),
#    url(r'^signin/$', login_forbidden(views.userLogin), name='user_signin'),
    url(r'^$', appHomePage, name='app_homepage'),

]
