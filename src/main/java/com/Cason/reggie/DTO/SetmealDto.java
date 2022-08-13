package com.Cason.reggie.DTO;

import com.Cason.reggie.entity.Setmeal;
import com.Cason.reggie.entity.SetmealDish;
import com.Cason.reggie.entity.Setmeal;
import com.Cason.reggie.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
