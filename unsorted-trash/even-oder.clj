;;
;;     MM""""""""`M
;;     MM  mmmmmmmM
;;     M`      MMMM 88d8b.d8b. .d8888b. .d8888b. .d8888b.
;;     MM  MMMMMMMM 88''88'`88 88'  `88 88'  `"" Y8ooooo.
;;     MM  MMMMMMMM 88  88  88 88.  .88 88.  ...       88
;;     MM        .M dP  dP  dP `88888P8 '88888P' '88888P'
;;     MMMMMMMMMMMM
;;
;;         M""MMMMMMMM M""M M""MMMMM""M MM""""""""`M
;;         M  MMMMMMMM M  M M  MMMMM  M MM  mmmmmmmM
;;         M  MMMMMMMM M  M M  MMMMP  M M`      MMMM
;;         M  MMMMMMMM M  M M  MMMM' .M MM  MMMMMMMM
;;         M  MMMMMMMM M  M M  MMP' .MM MM  MMMMMMMM
;;         M         M M  M M     .dMMM MM        .M
;;         MMMMMMMMMMM MMMM MMMMMMMMMMM MMMMMMMMMMMM  Version 1.0beta24
;;
;;           http://github.com/overtone/emacs-live
;;
;; Jr, turn your head towards the sun and the shadows will fall behind you.


(defonce inst-group (group))
(defonce fx-group (group [:after inst-group]))
(kill inst-group)
(kill fx-group)

(defonce reverb-bus (audio-bus))
((pp-node-tree))



(defonce sig-gr (group "signal-group"))
(defonce fx-gr  (group "fx-group" :after sig-gr))

(do
  (def patt-1 (buffer 4))
  (def patt-2 (buffer 4)))

(buffer-write! patt-1 [1 0 1 0])
(buffer-write! patt-2 [0 0 0 1])

(defonce t-bus (control-bus 1))

(defsynth ticker [rate 16 d 16 out-bus 0]
  (let [t (impulse:kr rate)
        td (pulse-divider t d)]
    (out out-bus t)
    (send-trig td 1 1)))

(def ticker-1 (ticker [:head sig-gr] :out-bus t-bus))
(pp-node-tree)
(kill ticker-1)

(defsynth kika
  [in-bus  0
   pat-buf 0
   on      1
   div     8
   m       1000]
  (let [t (in:kr in-bus)
        td (pulse-divider:kr t div)
        gn (mod (pulse-count:kr td) (buf-frames:kr pat-buf))
        g  (* td (buf-rd:kr 1 pat-buf gn))
        m-env (env-gen (adsr 0 0 1 2.4 1 0 0) on 1 0 1 FREE)
        a-env (env-gen (perc 0 0.3 1 -5) g 1 0 1)
        f-env (env-gen (perc 0 0.2 m -50) g 1 40 1)
        d-env (env-gen (perc 0 0.2 4 -15) g 1 1 1)
        sig   (* a-env (square f-env))
        flt   (resonz sig f-env 0.8)
        reverb (free-verb flt 0.3 0.7 0.8)
        d     (distort (* d-env (mix [reverb flt])))
        out-sig (* d m-env)]
    (out 0 (pan2 out-sig))))

(def kika1 (kika [:tail sig-gr] :in-bus t-bus :pat-buf pat-3))
(kill kika1)
(ctl kika1 :div 16)
(ctl ticker-1 :rate 0)
(odoc buffer-set!)
(odoc buffer-write-relay!)
(buffer-write! patt-1 (take 12 (cycle [1 0 0 1 0])))
(def pat-3 (buffer 16))

(do

  (buffer-write! pat-3 (take 16 (cycle [1 0 1 0])))
  (ctl kika1 :pat-buf pat-3  ))

(buffer-write! pat-3 (take 8 (cycle [1 1 0 0 1 1])))



(kill kika1)
(def pat-4 (buffer 18))

(on-event "/tr" (fn [m]  (do
                          (ctl kika2 :div (choose [2 4 8 16 1 32 48 128]))
                          (ctl kika1 :div (choose [2 4 8 16 1 32 64 256]))
                          (ctl kika1 :m (rand 15000))
                          (ctl kika2 :m (rand 13000))
                          (buffer-write! pat-3 (take 16 (repeatedly (fn [] (if (> (rand 100) 80) 1 0)))))
                          (buffer-write! pat-4 (take 18 (repeatedly (fn [] (if (> (rand 100) 40) 1 0)))))))
          ::change-params)

(remove-event-handler ::change-params)


(ctl kika2 :div 1)
(ctl kika1 :div 1)
(kill kika2)
(kill kika1)


(find-doc "event")

(do
  (def kika1 (kika [:tail sig-gr] :in-bus t-bus :pat-buf pat-3 :m 1000))
  (def kika2 (kika [:tail sig-gr] :in-bus t-bus :pat-buf pat-4 :m 300)))
