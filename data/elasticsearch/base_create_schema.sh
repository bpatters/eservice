curl -XGET http://app.dev:9200/_cluster/health?wait_for_status=green
curl -XPUT http://app.dev:9200/base 2>&1
sleep 1
curl -XPOST http://app.dev:9200/base/_close 2>&1
sleep 1
curl -XPUT http://app.dev:9200/base/_settings -T settings.json 2>&1
sleep 1
curl -XPUT http://app.dev:9200/base/user/_mapping -T base_user_schema.json 2>&1
sleep 1
curl -XPOST http://app.dev:9200/base/_open 2>&1
