package ru.practicum.shareit.item;

import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    Item toModel(ItemDTO itemDTO);

    ItemDTO toDTO(Item item);

    List<ItemDTO> toListDTO(List<Item> modelList);

    List<Item> toModelList(List<ItemDTO> itemDTOList);
}
