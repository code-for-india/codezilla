from django.shortcuts import render
from geopy.distance import vincenty
from .models import *
import json
from django.http import HttpResponse, HttpResponseRedirect, HttpResponseForbidden
from django.views.decorators.csrf import csrf_exempt
# Create your views here.
#>>> from geopy.distance import vincenty
#>>> newport_ri = (41.49008, -71.312796)
#>>> cleveland_oh = (41.499498, -81.695391)
#>>> print(vincenty(newport_ri, cleveland_oh).miles)
#@csrf_exempt

from math import radians, cos, sin, asin, sqrt, atan2
def distance_cal(lat1, lon1, lat2, lon2):
    # convert decimal degrees to radians 
    # approximate radius of earth in km
    R = 6373.0

    lat1 = radians(lat1)
    lon1 = radians(lon1)
    lat2 = radians(lat2)
    lon2 = radians(lon2)

    dlon = lon2 - lon1
    dlat = lat2 - lat1

    a = sin(dlat / 2)**2 + cos(lat1) * cos(lat2) * sin(dlon / 2)**2
    c = 2 * atan2(sqrt(a), sqrt(1 - a))

    distance = R * c
    return distance

def appHomePage(request):
    data=request.GET
    lat=float(data['lat'])
    lon=float(data['lon'])
    toilets=Toilets.objects.all()
    json_obj=[]
    for i in toilets:
        distance=distance_cal(lat,lon,i.lat,i.lon)
        if distance<=1:
            json_data={
                    "location_id":i.id,
                    "location_string":i.location_one_line(),
                    "latitude":i.lat,
                    "longitude":i.lon,
                    "rating":i.star,
                    "distance":distance,
                    }
            json_obj.append(json_data)
    return  HttpResponse(json.dumps({"feed":json_obj}),content_type="application/json")

def submitPhoto(request):
    try:
        data=request.POST
        image=data['photo']
        comment=data['comment']
        lat=data['lat']
        lon=data['lon']
        toilet_id=data['toilet_id']
        staff_id=data['staff_id']
        user=User.objects.get(id=staff_id)
        toilet=Toilets.objects.get(id=toilet_id)
        photo=Photo()
        photo.user=user
        photo.image=image
        photo.toilet=toilet
        photo.comment=comment
        photo.lat=lat
        photo.lon=lon
        photo.save()
        return  HttpResponse(json.dumps({"success":1,"msg":"successfully done"}),content_type="application/json")
    except Exception as e:
        return  HttpResponse(json.dumps({"success":0,"msg":str(e.message)}),content_type="application/json")

def visitToilet(request):
    data=request.GET
    toilet=Toilet.objects.get(id=data['tid'])
    if data['code']==1:
        return HttpResponse(json.dumps({"success":0,"data":toilet.visitor_current_count()}),content_type="application/json")
    if data['code']==2:
        return HttpResponse(json.dumps({"success":0,"data":toilet.visitor_total_count()}),content_type="application/json")
    if data['code']==3:
        return HttpResponse(json.dumps({"success":0,"data":toilet.reset_current_count()}),content_type="application/json")
    if data['code']==4:
        return HttpResponse(json.dumps({"success":0,"data":toilet.new_visitor()}),content_type="application/json")
    else:
        return HttpResponse(json.dumps({"success":0,"data":"send valid code"}),content_type="application/json")



'''
def loginValidate(request, data):
    email = data["email"]
    password = data['password']
    errors=[]
    try:
        user = User.objects.get(email=email)
    except:
        errors.append('user does not exist')
        return render(request,'login/signup.html',{'errors':errors})
    user = authenticate(username=email, password=password)
    if user is None:
        errors.append('password not correct')
        return render(request,'login/login.html',{'errors':errors})
    if user.is_active:
        login(request, user)
    else:
        errors.append('your account is suspended')
        return render(request,'login/signup.html',{'errors':errors})
    if 'is_ajax' in data:
        return redirect('checkout_page')  # redirect('home_page')
    else:
        return redirect('home_page')

def signupValidate(request, data):
    email = data["email"]
    if email:
        email = email.lower()
    password = data['password']
    cus = User.objects.filter(username=email).count()
    if cus > 0:
        return redirect('user_signin')
    cus = User.objects.create_user(email, email, password)
    cus.first_name = "first name"
    cus.last_name = "last name"
    cus.set_password(password)
    cus.is_active = True
    cus.save()
    if Cart.objects.filter(session_id=request.COOKIES['sessionid']).exists():
        cart = Cart.objects.get(session_id=request.COOKIES['sessionid'])
        cart.user = cus
        cart.save()
    try:
        profileinfo = Profile.objects.get(user=cus)
    except:
        profileinfo = Profile()
        profileinfo.user = cus
    profileinfo.Name = email
    profileinfo.email = email
    try:
        profileinfo.contact_no = data['phone']
    except:
        pass
    try:
        if int(data['signup_state']) == 1:
            profileinfo.profile_photo_url = data['photo_url']
        if int(data['signup_state']) == 2:
            profileinfo.profile_photo_url = data['photo_url']
    except:
        pass
    profileinfo.save()  # automatically send the welcome email
    return loginValidate(request, data)


def userSignup(request):
    errors=[]
    if request.method == 'GET':
        return render(request, 'login/signup.html')
    else:
        data = dict(request.POST.dict())
        if all(k in data for k in ("email", "password")):
            response = signupValidate(request, data)

        elif "email" in data:
            if not "password" in data:
                data['password'] = passwordGenerator()
                response = signupValidate(request, data)
                return response
            else:
                errors.append('Please send both Email and password')
                return render(request,'login/signup.html',{'errors':errors})

        return response


def userLogin(request):
    errors=[]
    if request.method == 'GET':
        return render(request, 'login/login.html')
    else:
        data = request.POST
        if not all(k in data for k in ("email", "password")):
            errors.append('Please send both Email and password')
            return render(request,'login/login.html',{'errors':errors})
        return loginValidate(request, data)


def userLogout(request):
    logout(request)
    return redirect('admin_page')

'''
