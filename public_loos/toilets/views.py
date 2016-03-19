from django.shortcuts import render
from geopy.distance import vincenty
from .models import *
import json
from django.http import HttpResponse, HttpResponseRedirect, HttpResponseForbidden
# Create your views here.
#>>> from geopy.distance import vincenty
#>>> newport_ri = (41.49008, -71.312796)
#>>> cleveland_oh = (41.499498, -81.695391)
#>>> print(vincenty(newport_ri, cleveland_oh).miles)

def appHomePage(request):
    data=request.GET
    lat=data['lat']
    lon=data['lon']
    toilets=Toilets.objects.all()
    json_obj=[]
    for i in toilets:
        distance=vincenty((float(lat),float(lon)), (float(i.lat),float(i.lon))).miles
        if distance<=1:
            selected.append(i)
            json_data={
                    "location_id":i.id,
                    "location_string":location_one_line(),
                    "latitude":i.lat,
                    "longitude":i.lon,
                    "rating":i.star,
                    "distance":distance,
                    }
            json_obj.append(json_data)
    return  HttpResponse(json.dumps(json_data),content_type="application/json")

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

