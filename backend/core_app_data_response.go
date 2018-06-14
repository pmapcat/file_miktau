package main

func (c CoreAppDataResponse) MetaCloud() map[string]int {
	meta_cloud := map[string]int{}
	for k, v := range c.Cloud {
		if IsMetaTag(k) {
			meta_cloud[k] = v
		}
	}
	return meta_cloud
}

func (c CoreAppDataResponse) MetaCloudContext() map[string]map[string]int {
	meta_cloud := map[string]map[string]int{}
	for k, v := range c.CloudContext {
		if IsMetaTag(k) {
			meta_cloud[k] = v
		}
	}
	return meta_cloud
}

func (c CoreAppDataResponse) MetaCloudCanSelect() map[string]bool {
	meta_cloud := map[string]bool{}
	for k, v := range c.CloudCanSelect {
		if IsMetaTag(k) {
			meta_cloud[k] = v
		}
	}
	return meta_cloud
}

func (c CoreAppDataResponse) SimpleCloud() map[string]int {
	meta_cloud := map[string]int{}
	for k, v := range c.Cloud {
		if !IsMetaTag(k) {
			meta_cloud[k] = v
		}
	}
	return meta_cloud
}

func (c CoreAppDataResponse) SimpleCloudContext() map[string]map[string]int {
	meta_cloud := map[string]map[string]int{}
	for k, v := range c.CloudContext {
		if !IsMetaTag(k) {
			meta_cloud[k] = v
		}
	}
	return meta_cloud
}

func (c CoreAppDataResponse) SimpleCloudCanSelect() map[string]bool {
	meta_cloud := map[string]bool{}
	for k, v := range c.CloudCanSelect {
		if !IsMetaTag(k) {
			meta_cloud[k] = v
		}
	}
	return meta_cloud
}
