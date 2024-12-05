"use client";

import {cn} from "../lib/utils";
import * as DropdownMenu from "@radix-ui/react-dropdown-menu";
import React, {ComponentPropsWithoutRef} from "react";

const Menu = DropdownMenu.Root;
const Trigger = DropdownMenu.Trigger;

interface IMenuContent
    extends ComponentPropsWithoutRef<typeof DropdownMenu.Content> {
}

const Content = ({className, ...props}: IMenuContent) => (
    <DropdownMenu.Portal>
        <DropdownMenu.Content
            side="bottom"
            align="start"
            className={cn(
                "bg-white border-2 border-black shadow-md absolute top-2 min-w-20",
                className
            )}
            {...props}
        />
    </DropdownMenu.Portal>
);

const MenuItem = React.forwardRef<
    HTMLDivElement,
    ComponentPropsWithoutRef<typeof DropdownMenu.Item>
>(({className, ...props}, ref) => (
    <DropdownMenu.Item
        ref={ref}
        className={cn(
            "relative flex cursor-default select-none items-center rounded-sm px-2 py-1.5 text-sm outline-none transition-colors hover:bg-primary-400 hover:text-white focus:bg-primary-400 focus:text-white data-[disabled]:pointer-events-none data-[disabled]:opacity-50",
            className
        )}
        {...props}
    />
));
MenuItem.displayName = "MenuItem";

const MenuComponent = Object.assign(Menu, {
    Trigger,
    Content,
    Item: MenuItem,
});

export {MenuComponent as Menu};