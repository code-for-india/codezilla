# -*- coding: utf-8 -*-
# Generated by Django 1.9.4 on 2016-03-19 06:30
from __future__ import unicode_literals

from django.conf import settings
from django.db import migrations, models
import django.db.models.deletion


class Migration(migrations.Migration):

    initial = True

    dependencies = [
        migrations.swappable_dependency(settings.AUTH_USER_MODEL),
    ]

    operations = [
        migrations.CreateModel(
            name='Complaint',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('star', models.IntegerField(default=5)),
            ],
        ),
        migrations.CreateModel(
            name='ComplaintText',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('text', models.TextField(max_length=500)),
            ],
        ),
        migrations.CreateModel(
            name='Photo',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('image', models.ImageField(upload_to='toilets/')),
                ('time', models.DateTimeField(auto_now_add=True)),
                ('comment', models.TextField(max_length=500)),
                ('lat', models.CharField(max_length=30)),
                ('lon', models.CharField(max_length=30)),
            ],
        ),
        migrations.CreateModel(
            name='Profile',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('email', models.EmailField(max_length=254)),
                ('name', models.CharField(max_length=255)),
                ('contact_no', models.CharField(max_length=15, unique=True)),
                ('device_id', models.CharField(max_length=255)),
                ('profile_photo', models.ImageField(upload_to='users/profile')),
                ('profile_type', models.IntegerField(choices=[(1, 'SWEEPER'), (2, 'ADMIN'), (3, 'NORMAL PEOPLE')], default=1)),
                ('last_visited', models.DateTimeField(auto_now=True)),
                ('email_verified', models.BooleanField(default=0)),
                ('phone_verified', models.BooleanField(default=0)),
                ('user', models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, to=settings.AUTH_USER_MODEL)),
            ],
        ),
        migrations.CreateModel(
            name='Toilets',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('area', models.TextField(max_length=255)),
                ('lat', models.CharField(max_length=12)),
                ('lon', models.TextField()),
                ('toilet_type', models.IntegerField(choices=[(0, 'Male'), (1, 'Female'), (2, 'Both')])),
                ('visits', models.IntegerField(default=0)),
                ('last_equipment_check', models.DateTimeField()),
                ('star', models.IntegerField(default=5)),
            ],
        ),
        migrations.AddField(
            model_name='complaint',
            name='text',
            field=models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, to='toilets.ComplaintText'),
        ),
        migrations.AddField(
            model_name='complaint',
            name='toilet',
            field=models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, to='toilets.Toilets'),
        ),
        migrations.AddField(
            model_name='complaint',
            name='user',
            field=models.ForeignKey(blank=True, null=True, on_delete=django.db.models.deletion.CASCADE, related_name='complaints', to=settings.AUTH_USER_MODEL),
        ),
    ]
