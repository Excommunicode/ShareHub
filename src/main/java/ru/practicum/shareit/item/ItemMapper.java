package ru.practicum.shareit.item;

import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    ItemDTO toDTO(Item item);

    Item toModel(ItemDTO itemDTO);

    List<ItemDTO> toListDTO(List<Item> modelList);

    List<Item> toModelList(List<ItemDTO> itemDTOList);
}
