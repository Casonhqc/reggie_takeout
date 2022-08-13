package com.Cason.reggie.service.impl;

import com.Cason.reggie.entity.AddressBook;
import com.Cason.reggie.mapper.AddressBookMapper;
import com.Cason.reggie.service.AddressBookService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook>implements AddressBookService {
}
