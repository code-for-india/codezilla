from __future__ import unicode_literals
from django.db import models
# Create your models here.
from django.contrib.auth.models import User
from django.db.models.signals import post_save
from django.db.models import Avg
class Profile(models.Model):
    PROFILE_TYPE_CHOICES = (
        (1, 'SWEEPER'),
        (2, 'ADMIN'),
        (3, 'NORMAL PEOPLE'),
    )

    user = models.ForeignKey(User)
    email = models.EmailField()
    name = models.CharField(max_length=255)
    contact_no = models.CharField(max_length=15,unique=True)
    device_id = models.CharField(max_length=255,null=True,blank=True)
    profile_photo=models.ImageField(upload_to='users/profile')
    profile_type = models.IntegerField(choices=PROFILE_TYPE_CHOICES, default=1)
    last_visited = models.DateTimeField(auto_now=True)
    email_verified = models.BooleanField(default=0)
    phone_verified = models.BooleanField(default=0)

    def save(self):
        user = self.user
        user.first_name = self.name
        user.save()
        super(Profile, self).save()

TOILET_TYPES = (
        (0, 'Male'),
        (1, 'Female'),
        (2, 'Both'),
    )
class Toilets(models.Model):
    area=models.TextField(max_length=255)
    lat=models.FloatField(default=0.0)
    lon=models.FloatField(default=0.0)
    toilet_type=models.IntegerField(choices=TOILET_TYPES)
    total_visits=models.IntegerField(default=0)
    current_visits=models.IntegerField(default=0)
    last_equipment_check=models.DateTimeField()
    star=models.IntegerField(default=5)
    def location_one_line(self):
        return str(self.area)+","+str(self.lat)+","+str(self.lon)

    def visitor_current_count(self):
        return self.current_visits

    def visitor_total_count(self):
        return self.current_visits

    def reset_current_count(self):
        self.current_visits=0
        self.save()
        return True

    def new_visitor(self):
        self.current_visits+=1
        self.save()
        return True

class Photo(models.Model):
    user=models.ForeignKey(User)
    image=models.ImageField(upload_to="toilets/")
    toilet=models.ForeignKey(Toilets, related_name="photos")
    time=models.DateTimeField(auto_now_add=True)
    comment=models.TextField(max_length=500)
    lat=models.FloatField(default=0.0)
    lon=models.FloatField(default=0.0)

class ComplaintText(models.Model):
    text=models.TextField(max_length=500)

class Complaint(models.Model):
    text=models.ForeignKey(ComplaintText)
    user=models.ForeignKey(User, null=True, blank=True, related_name="complaints")
    toilet=models.ForeignKey(Toilets)
    star=models.IntegerField(default=5)

def updateStar(sender, instance,**kwargs):
    obj=instance.toilet
    average=Complaint.objects.filter(toilet=obj).aggregate(Avg('star'))
    obj.star=average
    obj.save()
    return True
post_save.connect(updateStar, sender=Complaint)

