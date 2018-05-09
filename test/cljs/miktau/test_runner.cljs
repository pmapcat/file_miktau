(ns miktau.test-runner
  (:require
   [doo.runner :refer-macros [doo-tests]]
   [miktau.app-test]
   [miktau.events-test]
   [miktau.query-building-test]
   [miktau.various-test]
   [miktau.demo-data-test]
   [miktau.subs-test]))

(enable-console-print!)

(doo-tests 'miktau.app-test
           'miktau.events-test
           'miktau.query-building-test
           'miktau.various-test
           'miktau.subs-test
           'miktau.demo-data-test)

