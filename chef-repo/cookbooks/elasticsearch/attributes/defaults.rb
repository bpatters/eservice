# Version of elasticsearch to install
default[:elasticsearch][:package_version] = "1.4.2"

# version of cloud_aws plugin to install
default[:elasticsearch][:cloud_aws][:version] = "2.5.0"

# /etc/default/elasticsearch
default[:elasticsearch][:heap_size] = "1G"
default[:elasticsearch][:heap_newsize] = "256M"

# Index configuration
default[:elasticsearch][:index][:mapper][:dynamic] = "false"
default[:elasticsearch][:action][:auto_create_index] = "false"

# /etc/elasticsearch/elasticsearch.yml
default[:elasticsearch][:cluster_name] = "es-" + node[:app_env]
default[:elasticsearch][:node_name] = node[:hostname]
default[:elasticsearch][:data_node] = "true"
default[:elasticsearch][:master] = "true"
default[:elasticsearch][:rack] = "rack1"
default[:elasticsearch][:base_data_dir] = "/mnt/elasticsearch"
default[:elasticsearch][:log_dir] = "/var/log/elasticsearch"
default[:elasticsearch][:mlockall] = "true"
default[:elasticsearch][:http_enabled] = "true"
default[:elasticsearch][:discovery][:multicast_enabled] = "false"
default[:elasticsearch][:ec2_enabled] = false
default[:elasticsearch][:zen][:minimum_master_nodes] = 1
# By default, restart elasticsearch on config file change
default[:elasticsearch][:restart] = true
# Calculation: the max of 1 and 1 less than half the cluster size.  This is best
# suited to single-node dev/test deployments AND multi-AZ production deployments.
default[:elasticsearch][:gateway][:recover_after_nodes] = [(Integer(node[:elasticsearch][:cluster_size]) / 2) - 1, 1].max
default[:elasticsearch][:slowlog][:threshold][:query_warn] = "10s"
default[:elasticsearch][:slowlog][:threshold][:query_info] = "5s"
default[:elasticsearch][:slowlog][:threshold][:query_debug] = "2s"
default[:elasticsearch][:slowlog][:threshold][:query_trace] = "500ms"
default[:elasticsearch][:slowlog][:threshold][:fetch_warn] = "1s"
default[:elasticsearch][:slowlog][:threshold][:fetch_info] = "800ms"
default[:elasticsearch][:slowlog][:threshold][:fetch_debug] = "500ms"
default[:elasticsearch][:slowlog][:threshold][:fetch_trace] = "200ms"
default[:elasticsearch][:slowlog][:threshold][:index_warn] = "10s"
default[:elasticsearch][:slowlog][:threshold][:index_info] = "5s"
default[:elasticsearch][:slowlog][:threshold][:index_debug] = "2s"
default[:elasticsearch][:slowlog][:threshold][:index_trace] = "500ms"

# /etc/elasticsearch/logging.yml
default[:elasticsearch][:logging][:default_level] = "INFO"
default[:elasticsearch][:logging][:slowlog][:base] = "false"
default[:elasticsearch][:logging][:slowlog][:index] = "false"
