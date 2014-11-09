(ns arachnid
  (:refer-clojure :exclude [get])
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs-http.client :as http]
            [cljs-http.core :as http-core]
            [cljs-http.util :as http-util]
            [cljs.core.async :as async]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; core

(defrecord Resource
    [rel attrs links subresources controls])

(defrecord ResourceList
    [rel members])

(defrecord Link
    [rel uri options])

(defrecord Control
    [name href title method media_type fields])

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; hal

(declare hal->Resource)

(defn hal->Link [[rel link]]
  (Link. rel (:href link) (dissoc link :href)))

(defn hal->ResourceList [rel members]
  (ResourceList. rel
                 (map hal->Resource members)))

(defn hal->Subresource [[rel subresource]]
  (if (map? subresource)
    (assoc (hal->Resource subresource) :rel rel)
    (hal->ResourceList rel subresource)))


(defn hal->Control [[name control]]
  (map->Control (assoc control :name name)))

(defn hal->Resource [hal]
  (Resource. nil
             (dissoc hal :_links :_embedded :_controls)
             (map hal->Link        (:_links hal))
             (map hal->Subresource (:_embedded hal))
             (map hal->Control     (:_controls hal))))

(defn decode-hal [json]
  (-> json
      http-util/json-decode
      hal->Resource))

(defn wrap-hal-request
  "Set Accept: application/hal+json header."
  [client]
  (fn [request]
    (client (assoc request :accept "application/halo+json"))))

(defn wrap-hal-response
  "Decode application/hal+json responses."
  [client]
  (fn [request]
    (-> #(http/decode-body % decode-hal "application/halo+json" (:request-method request))
        (async/map [(client request)]))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; client

(defn wrap-without-credentials
  [client]
  (fn [request]
    (client (assoc request :with-credentials? false))))

(defn wrap-request [req]
  (-> req
      http/wrap-accept
      wrap-hal-request
      http/wrap-content-type
      wrap-hal-response
      wrap-without-credentials
      http/wrap-method
      http/wrap-url
      http/wrap-channel-from-request-map))

(def request (wrap-request http-core/request))

(defn delete
  "Like #'request, but sets the :method and :url as appropriate."
  [url & [req]]
  (request (merge req {:method :delete :url url})))

(defn get
  "Like #'request, but sets the :method and :url as appropriate."
  [url & [req]]
  (request (merge req {:method :get :url url})))

(defn head
  "Like #'request, but sets the :method and :url as appropriate."
  [url & [req]]
  (request (merge req {:method :head :url url})))

(defn move
  "Like #'request, but sets the :method and :url as appropriate."
  [url & [req]]
  (request (merge req {:method :move :url url})))

(defn options
  "Like #'request, but sets the :method and :url as appropriate."
  [url & [req]]
  (request (merge req {:method :options :url url})))

(defn patch
  "Like #'request, but sets the :method and :url as appropriate."
  [url & [req]]
  (request (merge req {:method :patch :url url})))

(defn post
  "Like #'request, but sets the :method and :url as appropriate."
  [url & [req]]
  (request (merge req {:method :post :url url})))

(defn put
  "Like #'request, but sets the :method and :url as appropriate."
  [url & [req]]
  (request (merge req {:method :put :url url})))

(defn get-form [resource name]
  (first (filter #(= (:name %) name) (:controls resource))))

(defn encode-body [media_type values]
  (if (= media_type "application/x-www-form-urlencoded")
    (http/generate-query-string values)))

(defn submit-form [form values]
  (request {:method (:method form)
            :url (:href form)
            :content-type (:media_type form)
            :body (encode-body (:media_type form) values)}))


(comment
  (go
    (let [response (<! (get "http://localhost:4567/groups/1" ))]
      (-> response
          :body
          (get-form :create)
          (submit-form {:name_1 "Ringo" :email_1 "star@beatles.io"})
          prn))))
