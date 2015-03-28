#
# Cookbook Name:: elasticsearch
# Recipe:: default
#
# Copyright 2015, YOUR_COMPANY_NAME
#
# All rights reserved - Do Not Redistribute
#

remote_file "#{Chef::Config[:file_cache_path]}/elasticsearch-1.4.2.deb" do
  source "https://download.elasticsearch.org/elasticsearch/elasticsearch/elasticsearch-1.4.2.deb"
end

dpkg_package "elasticsearch-1.4.2" do
  action :install
  source "#{Chef::Config[:file_cache_path]}/elasticsearch-1.4.2.deb"
end;

# Base Elasticsearch Directory
directory node[:elasticsearch][:base_data_dir] do
  action :create
  recursive true
  owner "elasticsearch"
  group "elasticsearch"
end

# install the config files
templates = %w{/etc/default/elasticsearch /etc/elasticsearch/elasticsearch.yml /etc/elasticsearch/logging.yml}
templates.each do |tmpl|
  template tmpl do
    source "#{tmpl}.erb"
    action :create
    mode "0644"
    notifies :restart, "service[elasticsearch]"
  end
end

# Registering the existence of the "elasticsearch" service so that the
# notifies can find it to call :restart.
service "elasticsearch" do
    supports :restart => true, :enabled => true, :start => true
    provider Chef::Provider::Service::Init::Debian
    action [ :enable, :start]
end
