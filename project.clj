(defproject miktau "0.1.0"
  :description "This is a METATOR project. "
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/clojurescript "1.10.238" :scope "provided"]
                 [pjstadig/humane-test-output "0.8.3"    :scope "test"]
                 [com.cognitect/transit-clj "0.8.309"]
                 [ring "1.6.3"]
                 ;; [eftest "0.5.1"]
                 [ring/ring-defaults "0.3.1"]
                 [bk/ring-gzip "0.3.0"]
                 [radicalzephyr/ring.middleware.logger "0.6.0"]
                 [clj-logging-config "1.9.12"]
                 [compojure "1.6.1"]
                 [environ "1.1.0"]
                 [day8.re-frame/http-fx "0.1.6"]
                 [cljs-ajax "0.7.3"]
                 [day8.re-frame/re-frame-10x "0.3.3-react16"]                 
                 [com.stuartsierra/component "0.3.2"]
                 [org.danielsz/system "0.4.1"]
                 [day8.re-frame/undo "0.3.2"]
                 [org.clojure/tools.namespace "0.2.11"]
                 [re-frame "0.10.5"]
                 [com.andrewmcveigh/cljs-time "0.5.2"]
                 [lambdaisland/garden-watcher "0.3.2"]]
  :plugins [[lein-cljsbuild "1.1.7"]
            [lein-environ "1.1.0"]
            [cider/cider-nrepl "0.17.0"]]
  

  :min-lein-version "2.6.1"

  :source-paths ["src/clj" "src/cljs" "src/cljc"]

  :test-paths ["test/clj" "test/cljc"]

  :clean-targets ^{:protect false} [:target-path :compile-path "resources/public/js" "dev-target"]

  :uberjar-name "miktau.jar"

  ;; Use `lein run` if you just want to start a HTTP server, without figwheel
  :main miktau.application

  ;; nREPL by default starts in the :main namespace, we want to start in `user`
  ;; because that's where our development helper functions like (go) and
  ;; (browser-repl) live.
  :repl-options {:init-ns user}

  :cljsbuild {:builds
              [{:id "app"
                :source-paths ["src/cljs" "src/cljc" "dev"]

                :figwheel {:on-jsload "miktau.system/reset"}

                :compiler {:main cljs.user
                           :asset-path "js/compiled/out"
                           :output-to "dev-target/public/js/compiled/miktau.js"
                           :output-dir "dev-target/public/js/compiled/out"
                           :closure-defines      {"re_frame.trace.trace_enabled_QMARK_" true}
                           :preloads             [day8.re-frame-10x.preload]
                           :source-map-timestamp true}}

               {:id "test"
                :source-paths ["src/cljs" "test/cljs" "src/cljc" "test/cljc"]
                :compiler {:output-to "dev-target/public/js/compiled/testable.js"
                           :main miktau.test-runner
                           :optimizations :none}}

               {:id "min"
                :source-paths ["src/cljs" "src/cljc"]
                :jar true
                :compiler {:main miktau.system
                           :output-to "resources/public/js/compiled/miktau.js"
                           :output-dir "target"
                           :source-map-timestamp true
                           :optimizations :advanced
                           :closure-defines {goog.DEBUG false}
                           :pretty-print false}}]}

  ;; When running figwheel from nREPL, figwheel will read this configuration
  ;; stanza, but it will read it without passing through leiningen's profile
  ;; merging. So don't put a :figwheel section under the :dev profile, it will
  ;; not be picked up, instead configure figwheel here on the top level.

  :figwheel {;; :http-server-root "public"       ;; serve static assets from resources/public/
             ;; :server-port 3449                ;; default
             ;; :server-ip "127.0.0.1"           ;; default
             :css-dirs ["resources/public/css"]  ;; watch and update CSS

             ;; Start an nREPL server into the running figwheel process. We
             ;; don't do this, instead we do the opposite, running figwheel from
             ;; an nREPL process, see
             ;; https://github.com/bhauman/lein-figwheel/wiki/Using-the-Figwheel-REPL-within-NRepl
             ;; :nrepl-port 7888

             ;; To be able to open files in your editor from the heads up display
             ;; you will need to put a script on your path.
             ;; that script will have to take a file path and a line number
             ;; ie. in  ~/bin/myfile-opener
             ;; #! /bin/sh
             ;; emacsclient -n +$2 $1
             ;;
             ;; :open-file-command "myfile-opener"

             :server-logfile "log/figwheel.log"}

  :doo {:build "test"}

  :profiles {:dev
             {:dependencies [[figwheel "0.5.15"]
                             [figwheel-sidecar "0.5.15"]
                             [com.cemerick/piggieback "0.2.2"]
                             [org.clojure/tools.nrepl "0.2.13"]
                             [lein-doo "0.1.10"]
                             [reloaded.repl "0.2.4"]
                             [day8.re-frame/re-frame-10x "0.3.3"]]

              :plugins [[lein-figwheel "0.5.15"]
                        [lein-doo "0.1.10"]]

              :source-paths ["dev"]
              :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}}

             :uberjar
             {:source-paths ^:replace ["src/clj" "src/cljc"]
              :prep-tasks ["compile"
                           ["cljsbuild" "once" "min"]
                           ["run" "-m" "garden-watcher.main" "miktau.styles"]]
              :hooks []
              :omit-source true
              :aot :all}})
