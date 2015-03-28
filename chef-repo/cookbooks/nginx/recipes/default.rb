#
# Cookbook Name:: nginx
# Recipe:: default
#
# Copyright 2015, YOUR_COMPANY_NAME
#
# All rights reserved - Do Not Redistribute
#
include_recipe "apt::update"

package 'nginx' do
  action :install
end

template "/etc/nginx/sites-available/default" do
  source "default/sites-available/default.erb"
  mode "0644"
  action :create
end

template "/etc/nginx/nginx.conf" do
  source "default/nginx.conf.erb"
  mode "0644"
  action :create
end

service 'nginx' do
  action [ :enable, :stop, :start ]
end
