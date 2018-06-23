package main

import (
	"errors"
	"github.com/boltdb/bolt"
	"os"
	"path/filepath"
)

type patch_db_ struct{}

var patch_db = patch_db_{}

func (p *patch_db_) _storeData(root string, pr []*PatchRecord) error {
	// not applied on :memory:
	if root == IN_MEMORY_DB_PATH {
		return nil
	}

	os.MkdirAll(root, 0777)
	patchdb := filepath.Join(root, PATCH_DB_PREFIX)

	db, err := bolt.Open(patchdb, 0777, nil)
	if err != nil {
		return err
	}
	defer db.Close()
	return db.Update(func(tx *bolt.Tx) error {
		bucket, err := tx.CreateBucketIfNotExists([]byte(PATCH_DB_BUCKET))
		if err != nil {
			return err
		}
		for _, item := range pr {
			datum, err := item.MarshalBinary()
			if err != nil {
				LogErr("[_storeData]", err)
				continue
			}
			bucket.Put([]byte(item.RelativePath), datum)
		}
		return nil
	})
}

func (p *patch_db_) _retrieveData(root string, keys []string, cb func(string, *PatchRecord)) error {
	if root == IN_MEMORY_DB_PATH || root == EMPTY_DATA_PATH {
		return nil
	}

	patchdb := filepath.Join(root, PATCH_DB_PREFIX)

	db, err := bolt.Open(patchdb, 0777, nil)
	if err != nil {
		return err
	}
	defer db.Close()
	return db.View(func(tx *bolt.Tx) error {
		bucket := tx.Bucket([]byte(PATCH_DB_BUCKET))
		if bucket == nil {
			return errors.New("No bucket")
		}
		for _, item := range keys {
			pr := PatchRecord{}
			if err := LogErr("Cannot unmarshal data", pr.UnmarshalBinary(bucket.Get([]byte(item)))); err != nil {
				continue
			}
			cb(string(item), &pr)
		}
		return nil
	})
}

func (p *patch_db_) BuildRetrieveSaved(core_dir string) func([]*AppStateItem) {
	if core_dir == IN_MEMORY_DB_PATH || core_dir == EMPTY_DATA_PATH {
		return func([]*AppStateItem) {}
	}

	return func(items []*AppStateItem) {

		keys := []string{}
		orwork := map[string]*AppStateItem{}
		for _, v := range items {
			res, err := RelativePath(core_dir, v.FilePath)
			if err != nil {
				LogErr("[BuildRetrieveSaved] Cannot resolve root within: ", err)
				continue
			}
			keys = append(keys, res)
			orwork[res] = v
		}
		LogErr("[BuildRetrieveSaved] cannot retrieve data", p._retrieveData(core_dir, keys, func(key string, pr *PatchRecord) {
			item, ok := orwork[key]
			if !ok {
				return
			}
			if len(pr.Tags) > 0 {
				item.Tags = pr.Tags
			}
		}))
	}
}
func (p *patch_db_) BuildStoreExisting(core_dir string) func([]*AppStateItem) {
	if core_dir == IN_MEMORY_DB_PATH {
		return func([]*AppStateItem) {}
	}

	return func(items []*AppStateItem) {
		reso := []*PatchRecord{}
		for _, v := range items {
			rp, err := RelativePath(core_dir, v.FilePath)
			if err != nil {
				LogErr("[BuildStoreExisting] Cannot make relative path", err)
				continue
			}
			if len(v.Tags) > 0 {
				reso = append(reso, &PatchRecord{RelativePath: rp, Tags: v.Tags})
			}
		}
		LogErr("[BuildStoreExisting] cannot store app data", p._storeData(core_dir, reso))
	}
}
