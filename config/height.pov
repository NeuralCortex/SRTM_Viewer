#include "colors.inc"                                   
#include "textures.inc"
 
//E010N50.63E
 
camera {
    location <10, 10, -15>
    look_at  <0, 0,  0>  
    right 16/9*x
}   

light_source { <2, 5, -20> color White}     

plane{<0,1,0>,1 
    hollow  
    texture{
        pigment{
            bozo turbulence 0.85 scale 1.0 translate<5,0,0>
            color_map{
                 [0.5 rgb <0.20, 0.20, 1.0>]
                 [0.6 rgb <1,1,1>]
                 [1.0 rgb <0.5,0.5,0.5>]
            }
        }
        finish {ambient 1 diffuse 0} }      
        scale 10000
}  

#declare TileNormal=normal{
    gradient x 2 
    slope_map{
        [0 <0, 1>] 
        [.05 <1, 0>] 
        [.95 <1, 0>] 
        [1 <0, -1>]
    }
} 

plane { <0, 1, 0>, -5.2
    pigment{ 
        checker
        pigment { granite color_map { [0 rgb 1][1 rgb .9] } }
        pigment { granite color_map { [0 rgb .9][1 rgb .7] } }
    }
    finish { specular 1 }
    normal{
        average normal_map{
            [1 TileNormal]
            [1 TileNormal rotate y*90]
        }
    }
} 

#declare tex=pigment {
    gradient y      
    pigment_map {   
      [0.0 Blue]
      [0.2 Green]
      [0.3 Jade]    
      [0.6 Orange]
      [1.0 White]
      }
    }


height_field {
  png "#file"    
  scale <15,0,20>
  translate <-7.5,0,-7.5>  
  pigment{tex}
}

cylinder{
    <#x1,-1000,#z1>,
    <#x1,1000,#z1>,0.001
    pigment{color White}
    scale <15,0,20>
    translate <-7.5,0,-7.5>  
}
