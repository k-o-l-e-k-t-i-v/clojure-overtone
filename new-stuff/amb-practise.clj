;; this is result code of livecoding pratice by jrzn
;; this time I will attempt to create ambient stuff

(def pat-1 (ref [1 0 0 0 0 0 0 0]))
(def source-g (group))
(def metro-1 (metronome 180))
(amb-1)

(defsynth amb-1
  [n 40
   fb 0.3
   np 3
   fs 1
   fsh 200
   amp 0.3
   erl 0.3
   tal 0.3
   rom 100
   l   12
   ata 2
   inbus 0]
  (let [me (env-gen (perc ata l 1 0) 1 amp 0 1 FREE)
        f (midicps n)
        fnoise (+ (* 3 (lf-noise2 140)) f)
        s (sin-osc-fb fnoise fb)
        s2 (freq-shift s fsh (sin-osc (* fs f)))
        s3 (square (* 0.5 fnoise))
        s (mix [s s2 s3])
        s(resonz s (* (+ 6000 (* 4000 (lf-noise2 np)))
                   (env-gen (perc 0 4 1 0) 1 1.2 0.1 1)) 0.92)
        s (normalizer s)
        s (freq-shift s (* 5 (lf-noise2 (* fs fs))))
        s (g-verb s rom 4 0.2
                  :earlyreflevel erl
                  :taillevel tal )
        sme (* s me)
        ]
    (out 0 (pan2 sme (* 0.2 (lf-noise2 (* (lin-lin (lf-noise2 4) -1 1 0.2 6) 1))) 1))))

(defn a
  []
  (metro-bpm metro-1 (+ 40 (rand 60)))
  (let [lx (* 0.001 (metro-tock metro-1))
        r (rand 20)
        prob 12]
    (dotimes [n 3]
      (amb-1 [:head source-g]
             :n (+ (choose [30 42 54 66 78]) (* n 7) (* 0.001 (rand 100)))
             :fb (choose [0.2 0.3 0.1 0.7 2 3 6])
             :np (choose [3 9 30 1200 300])
             :fs (choose [1 1.1 1 2 3])
             :fsh (choose [2000 1000 4000 200 -200 -1000])
             :amp (* 0.6 (choose [0.3 0.4 0.2 0.1]))
             :erl (choose [0.2 0.4 0.1 0.6])
             :tal (choose [0.2 0.4 0.1 0.6])
             :rom (choose [100 80 130 200 270 30])
             ))
    (if (> r prob)
      (amb-1 :amp 0.6
             :n (choose [28 40 43 47 52])))))


(amb-1 :amp 0.6 :n 28)
(seq-player metro-1 (metro-1) @pat-1 pat-1 #'a)
(metro-bpm metro-1 90)
(stop)


()
