(ns httpkit-ssl-issue.core
  (:gen-class)
  (:require [org.httpkit.client :as http]))

;; solution from https://github.com/http-kit/http-kit/issues/228
;; but it does not help
;;
;; (:import (javax.net.ssl X509TrustManager SSLContext TrustManager)
;;          (java.security.cert X509Certificate))
;;
;; (defonce ssl-engine
;;   (let [tm (reify javax.net.ssl.X509TrustManager
;;              (getAcceptedIssuers [this] (make-array X509Certificate 0))
;;              (checkClientTrusted [this chain auth-type])
;;              (checkServerTrusted [this chain auth-type]))
;;         client-context (SSLContext/getInstance "TLSv1.2")]
;;     (.init client-context nil
;;            (-> (make-array TrustManager 1)
;;                (doto (aset 0 tm)))
;;            nil)
;;     (.createSSLEngine client-context)))
;;
;; (defn ssl-request
;;   [url]
;;   (http/request {:url url
;;                  :method :get
;;                  :sslengine ssl-engine}))

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
  (println error)
  (newline))

(defn -main
  [& args]
  (->> (or args default-urls)
       (map (comp print-details deref http/get))
       dorun))
