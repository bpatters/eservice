#
# Cookbook Name:: java
# Recipe:: default
#
# Copyright 2015, YOUR_COMPANY_NAME
#
# All rights reserved - Do Not Redistribute
#

apt_package "python-software-properties" do
  action :install
end

execute "add-apt-repository ppa:webupd8team/java" do
  command "add-apt-repository ppa:webupd8team/java"
end

#include_recipe "apt::update"
execute "apt-get update" do
  command "apt-get update"
end

execute "accept-license" do
  command "echo oracle-java8-installer shared/accepted-oracle-license-v1-1 select true | /usr/bin/debconf-set-selections"
end

apt_package "oracle-java8-installer" do
  action :install
end
