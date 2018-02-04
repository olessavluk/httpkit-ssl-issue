(ns httpkit-ssl-issue.core
  (:gen-class)
  (:require [org.httpkit.client :as http])
  (:import (javax.net.ssl SSLEngine SSLParameters SNIHostName)
           (java.net URI)))

;; from https://github.com/jiacai2050
;; works fine
(defn sni-configure
  [^SSLEngine ssl-engine ^URI uri]
  (let [^SSLParameters ssl-params (.getSSLParameters ssl-engine)]
    (.setServerNames ssl-params [(SNIHostName. (.getHost uri))])
    (.setSSLParameters ssl-engine ssl-params)))

(def client (http/make-client {:ssl-configurer sni-configure}))

(def default-urls
  ["https://google.com" ;; ok
   "https://clojure.org" ;; Received fatal alert: handshake_failure
   "https://letsencrypt.org" ;; unable to find valid certification path to requested target
   "https://httpbin.org/get" ;; Received fatal alert: internal_error
   ])

(defn print-details
  [{:keys [status error opts]}]
  (println (if (= 200 status) "✅ " "⛔ ")
           (:url opts))
  (if error (println error))
  (newline))

(def request http/get
  ;; #(http/get % {:client client})
  )

(defn -main
  [& args]
  (->> (or args default-urls)
       (map (comp print-details deref request))
       dorun))
