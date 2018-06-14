package main

import (
	log "github.com/sirupsen/logrus"
	"github.com/skratchdot/open-golang/open"
)

func OpenFile(fpath string) error {
	if is_dev_environ() {
		log.Info("We are in dev environment")
		log.Info("Successfully opened in a dev environ")
		return nil
	}
	return open.Run(fpath)
}
