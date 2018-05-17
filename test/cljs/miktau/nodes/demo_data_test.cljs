(ns miktau.nodes.demo-data-test)

(def demo-db
  {:nodes-sorted "-name"
   :nodes [{:id 0, :name "blab.mp4" :file-path "/home/mik/this_must_be_it/" :tags []
            :modified {:year 2016 :month 7 :day 21}}]
   :nodes-selected #{"*"}
   :meta {:page :nodes}

   :total-nodes 1
   
   :cloud-selected #{:blab}
   :calendar-selected {:year  2018  :day 23 :month 11}})

(def initial-db-after-load-from-server
  {:nodes-selected #{"*"},
   :nodes-sorted "",
   :cloud-selected #{:blab},
   :meta {:page :nodes}
   :total-nodes 22
   :nodes [{:id 0, :name "blab.mp4", :file-path "/home/mik/this_must_be_it/", :tags [], :modified {:year 2016, :month 7, :day 21}}
           {:id 1, :name "hello.mp4",:file-path "/home/mik/this_must_be_it/", :tags ["natan" "work" "bibliostore" "moscow_market"], :modified {:year 2017, :month 7, :day 20}}
           {:id 2, :name "blab.mp4", :file-path "/home/mik/this_must_be_it/", :tags ["natan" "work" "bibliostore" "moscow_market"], :modified {:year 2018, :month 7, :day 19}}
           {:id 3, :name "glib.mp4", :file-path "/home/mik/this_must_be_it/", :tags ["natan" "work" "bibliostore" "moscow_market"], :modified {:year 2016, :month 7, :day 18}}
           {:id 4, :name "blob.mp4", :file-path "/home/mik/chosen_one/", :tags ["natan" "work" "bibliostore" "translator"], :modified {:year 2017, :month 2, :day 17}}
           {:id 5, :name "plop.mp4", :file-path "/home/mik/chosen_one/", :tags ["natan" "work" "bibliostore" "moscow_market"], :modified {:year 2018, :month 2, :day 16}}
           {:id 6, :name "grop.mp4", :file-path "/home/mik/chosen_one/", :tags ["natan" "work" "bibliostore" "moscow_market"], :modified {:year 2016, :month 2, :day 15}}
           {:id 7, :name "drop.mp4", :file-path "/home/mik/chosen_one/", :tags ["natan" "work" "moscow_market" "amazon"], :modified {:year 2016, :month 2, :day 14}}
           {:id 8, :name "nrap.mp4", :file-path "/home/mik/this_must_be_it/", :tags ["natan" "work" "devops" "moscow_market" "bibliostore" "translator" "amazon"], :modified {:year 2016, :month 4, :day 13}}
           {:id 9, :name "zlip.mp4", :file-path "/home/mik/another/", :tags ["natan" "work" "wiki" "sforim" "согласовать"], :modified {:year 2017, :month 4, :day 12}}
           {:id 10, :name "zlop.mp4", :file-path "/home/mik/another/", :tags ["natan" "work" "moscow_market" "скачка_источников" "биржа"], :modified {:year 2017, :month 4, :day 11}}
           {:id 11, :name "zip.mp4", :file-path "/home/mik/another/", :tags ["natan" "work" "магазины" "sforim"], :modified {:year 2017, :month 4, :day 10}}
           {:id 12, :name "nop.mp4", :file-path "/home/mik/another/", :tags ["natan" "work" "магазины" "moscow_market" "bibliostore"], :modified {:year 2016, :month 2, :day 9}}
           {:id 13, :name "nar.mp4", :file-path "/home/mik/another/", :tags ["natan" "work" "биржа" "UI"], :modified {:year 2016, :month 2, :day 8}}
           {:id 14, :name "gor.mp4", :file-path "/home/mik/another/", :tags ["work" "personal"], :modified {:year 2016, :month 2, :day 7}}
           {:id 15, :name "dar.mp4", :file-path "/home/mik/figuratively/", :tags ["work" "personal" "blog"], :modified {:year 2016, :month 2, :day 5}}
           {:id 16, :name "gir.mp4", :file-path "/home/mik/figuratively/", :tags ["work" "personal" "usecases"], :modified {:year 2016, :month 5, :day 5}}
           {:id 17, :name "grar.mp4", :file-path "/home/mik/figuratively/", :tags ["work" "personal" "usecases"], :modified {:year 2016, :month 5, :day 4}}
           {:id 18, :name "grion.mp4", :file-path "/home/mik/figuratively/literally", :tags ["work" "zeldin"], :modified {:year 2016, :month 5, :day 3}}
           {:id 19, :name "grano.mp4", :file-path "/home/mik/figuratively/literally", :tags ["work" "zeldin"], :modified {:year 2016, :month 5, :day 2}}
           {:id 20, :name "dramo.mp4", :file-path "/home/mik/figuratively/literally", :tags ["work" "everybook"], :modified {:year 2016, :month 3, :day 1}}
           {:id 21, :name "blab.mp4", :file-path "/home/mik/figuratively/", :tags ["работа_сделана"], :modified {:year 2017, :month 1, :day 24}}],
   :calendar-selected   {:year 2018, :day 23, :month 11},})

