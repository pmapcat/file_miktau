package main

import (
	"errors"
	"github.com/boltdb/bolt"
	"os"
	"path/filepath"
	"strings"
)

// ### How would it look like (work)?
// On init (read):
// * Read first source of truth (File System)
// * Then, read "patch" from a database file (in the same directory):
//   * In patch ("what directories/tags are "removed")
//   * What directories "tags" are added.
//   * Apply patch

// On update (write):
// * If no Tags on patch
//   * Then, nothing was edited
//   * Then, load from FS
// * otherwise:
//   * load tags from patch

type patch_db_ struct{}

var patch_db = patch_db_{}

func (p *patch_db_) _retrievePatchData(root string) (map[string]*PatchRecord, error) {
	result := map[string]*PatchRecord{}

	patchdb := filepath.Join(root, PATCH_DB_PREFIX)

	db, err := bolt.Open(patchdb, 0777, nil)
	if err != nil {
		return result, err
	}
	defer db.Close()
	return result, db.View(func(tx *bolt.Tx) error {
		bucket := tx.Bucket(PATCH_DB_BUCKET)
		if bucket == nil {
			return errors.New("No bucket")
		}
		return bucket.ForEach(func(k []byte, v []byte) error {
			pr := PatchRecord{}
			if err := LogErr("Cannot unmarshal data", pr.UnmarshalBinary(v)); err != nil {
				return err
			}
			result[string(k)] = &pr
			return nil
		})
	})
}

func (p *patch_db_) MakePatch(root string, filepath string, tags []string) *PatchRecord {
	pr := PatchRecord{}
	newpath := strings.TrimPrefix(filepath, root)
	pr.RelativePath = newpath
	pr.Tags = tags
	return &pr
}

func (p *patch_db_) ApplyPatch(root string, items []*PatchRecord) error {
	os.MkdirAll(root, 0777)
	patchdb := filepath.Join(root, PATCH_DB_PREFIX)

	db, err := bolt.Open(patchdb, 0777, nil)
	if err != nil {
		return err
	}
	defer db.Close()
	return db.Update(func(tx *bolt.Tx) error {
		bucket, err := tx.CreateBucketIfNotExists(PATCH_DB_BUCKET)
		if err != nil {
			return err
		}
		for _, item := range items {
			datum, err := item.MarshalBinary()
			LogErr("Marshal error", err)
			if err != nil {
				continue
			}
			bucket.Put([]byte(item.RelativePath), datum)
		}
		return nil
	})
}

// will apply user patch to a data set
func (p *patch_db_) OnLoadApplyPatch(root string, items []*CoreNodeItem) ([]*CoreNodeItem, error) {
	patchdb, err := p._retrievePatchData(root)
	if err != nil {
		return items, err
	}
	for k, v := range items {
		patch, ok := patchdb[strings.TrimPrefix(v.FilePath, root)]
		if !ok {
			continue
		}
		v.Tags = patch.Tags
		items[k] = v
	}
	return items, nil
}
