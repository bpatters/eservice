# Environment Variables:

green_text="\033[32m"
red_text="\033[31m"
reset_colors="\033[0m"

# Check that required plugins are installed in a vagrant 1.5 manner
%w(vagrant-hostmanager).each do |plugin|
  unless Vagrant.has_plugin? plugin
    puts("#{red_text}This Vagrantfile requires #{plugin}.  You can fix this by running 'vagrant plugin install #{plugin}'.#{reset_colors}")
  end
end

app_version='hashicorp/precise64'
app_name = "appserver"
host_ip = "192.168.77.1"
app_private_ip = "192.168.77.2"

puts "Using app (#{green_text}#{app_version}#{reset_colors})"

Vagrant.configure("2") do |config|
  # set global virtualbox options
  config.vm.provider :virtualbox do |vb|
    vb.customize ["modifyvm", :id, "--natdnshostresolver1", "on"]
    vb.customize ["modifyvm", :id, "--vrde", "off"]
    vb.customize ["modifyvm", :id, "--vram", "12"]
    vb.customize [ "guestproperty", "set", :id, "/VirtualBox/GuestAdd/VBoxService/--timesync-set-threshold", 10000 ]
    vb.customize [ "guestproperty", "set", :id, "/VirtualBox/GuestAdd/VBoxService/--timesync-interval", 5000 ]
  end

    # configure global hostname settings
    config.hostmanager.enabled = true
    config.hostmanager.manage_host = true;
    config.hostmanager.ignore_private_ip = false
    config.hostmanager.include_offline = true

    # mount the code directory
    config.vm.synced_folder "ui/www", "/opt/www/", :nfs => true

 #################
  # 64-bit App VM #
  #################
  config.vm.define :app do |app|
    app.vm.box = app_version
    app.vm.hostname = "dev-app"
    app.vm.network :private_network, ip: app_private_ip

    app.vm.provider :virtualbox do |vb|
      vb.name = app_name
      vb.customize ["modifyvm", :id, "--memory", "2048"]
      vb.customize ["modifyvm", :id, "--cpus", "4"]
    end

    app.omnibus.chef_version = :latest

    # NGINX Network
    # Chef solo provisioning
    app.vm.provision :chef_solo do |chef|
      chef.cookbooks_path = "chef-repo/cookbooks/"
      chef.add_recipe "apt"
      chef.add_recipe "base"
      chef.add_recipe "nginx"
      chef.add_recipe "java"
      chef.add_recipe "elasticsearch"


      chef.json  = {
        :app_env => "dev",
        :elasticsearch => {
                :base_data_dir => "/mnt/elasticsearch",
                :heap_size => "1024M",
                :heap_newsize => "64M",
                :mlockall => "false",
                :restart => true,
                :cluster_size => 1,
                :replication_factor => 0,
                :shards => 1,
                :write_consistency => "ONE",
                :publish_host => app_private_ip,
              }
      }
    end
    app.vm.provision :shell, inline: 'sh -c "cd /vagrant/data/elasticsearch/;./base_create_schema.sh"'
    app.hostmanager.aliases = %w(devbox web.dev app.dev elastic.dev)

  end

end
