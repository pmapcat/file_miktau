package main

import (
	"errors"
	log "github.com/sirupsen/logrus"
	"os"
)

// Takes MUTABLE action on a file system
func (n *AppState) SideEffectFileSystemActionOnAListOfFiles(query Query, action string) error {
	fpathes := []string{}
	n.GetInBulk(query, func(item *AppStateItem) {
		fpathes = append(fpathes, item.FilePath)
	})
	switch action {
	case STRATEGY_SYMLINK:
		return fs_backend.OpenAsSymlinksInASingleFolder(fpathes)
	case STRATEGY_DEFAULT_PROGRAM:
		return fs_backend.OpenEachInDefaultProgram(fpathes)
	}
	return errors.New("No action was specified for this dataset")
}

type ResolverAppStateItem struct {
	Node         *AppStateItem
	CameWithPath string
	WithResolver os.FileInfo
}

// filepath: TODO: TEST
func (n *AppState) SideEffectMutablePushNewFiles(file_paths []string) ([]int, error) {
	// resolve files by comparing them to these already within the root FS
	resolved_items, unresolved_items, err := n.SideEffectResolveIfPossibleWithinFileSystem(file_paths)
	if err != nil {
		return []int{}, err
	}
	// take unresolved items, and extract strings from them
	unresolved_items_strings := []string{}
	for _, v := range unresolved_items {
		unresolved_items_strings = append(unresolved_items_strings, v.CameWithPath)
	}

	// symlink unresolved into <root> directory
	unresolved_as_new_pathes, err := fs_backend.SymlinkInRootGivenForeignPathes(n.core_dir, unresolved_items_strings)
	new_core_node_items := []*AppStateItem{}
	for _, item := range unresolved_as_new_pathes {
		finfo, err := os.Stat(item)
		if err != nil {
			LogErr("This is the error", err)
			continue
		}
		new_core_node_items = append(new_core_node_items, newAppStateItemFromFile(n.core_dir, finfo, item))
	}
	// create new records for these newly symlinked items into root directory
	result_ids := n.MutableCreate(new_core_node_items)

	// save ids from previous operation
	for _, v := range resolved_items {
		result_ids = append(result_ids, v.Node.Id)
	}
	// and return it back to the user
	return result_ids, nil
}

// Gathers data from FileSystem
// for each file, check whether it is already in
// This is O(N*K) algorithm, where K is the amount of file_pathes.
// On larger systems, it might take larger time to work with it
func (n *AppState) SideEffectResolveIfPossibleWithinFileSystem(file_paths []string) ([]*ResolverAppStateItem, []*ResolverAppStateItem, error) {
	finfos := []*ResolverAppStateItem{}
	for _, v := range file_paths {
		stat, err := os.Lstat(v)
		if err != nil {
			log.WithError(err).WithField("path", v).
				Error("Path is not resolvable, skipping")
			continue
		}
		finfos = append(finfos, &ResolverAppStateItem{CameWithPath: v, WithResolver: stat})
	}

	if len(finfos) == 0 {
		log.Info("Empty list of data, terminating prematurely, saving CPU cycles")
		return []*ResolverAppStateItem{}, []*ResolverAppStateItem{}, nil
	}

	results := []*ResolverAppStateItem{}
	for _, node := range n.nodes {
		for point_index, resolver_node := range finfos {
			if resolver_node == nil {
				continue
			}
			if os.SameFile(node.FileInfo, resolver_node.WithResolver) {
				resolver_node.Node = node
				results = append(results, resolver_node)
				finfos[point_index] = nil
			}
			break
		}
	}
	unresolved := []*ResolverAppStateItem{}
	for _, v := range finfos {
		if v != nil {
			unresolved = append(unresolved, v)
		}
	}
	return results, unresolved, nil
}
