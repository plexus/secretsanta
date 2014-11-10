(ns secretsanta-frontend.core
  (:refer-clojure :exclude [get])
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [cljs.core.async :refer [chan]]
            [arachnid :refer [get]]))

(defonce app-state (atom {:page :loading}))

(defn main-component [app owner]
  (reify
    om/IRender
    (render [_]
      (case (:page app)
        :loading (dom/h1 nil "Loading...")
        :home    (dom/h1 nil "Welcome to Secret Santa")))))

(defn main []
  (om/root
     main-component
     app-state
     {:target (. js/document (getElementById "app"))}))


(let [response (<! (get "http://localhost:4567/api"))]
  (swap! app-state #(assoc % :page :home))
  (swap! app-state #(assoc % :form
                           (get-form (:body response) :create))))


(comment
  (go
    (loop []
      (when-let [value (<! my-chan)]
        (swap! app-state #(assoc % :page value))
        (recur))))

  (go (>! my-chan :home))
  (go (>! my-chan :loading)))
