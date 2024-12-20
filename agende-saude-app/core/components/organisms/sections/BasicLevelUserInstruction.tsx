import { Link } from "expo-router";
import { Emphasis, FlatButton, Paragraph } from "../../atoms";
import { Flex } from "../../molecules";
import { Alert } from "../cards";
import React from "react";

const BasicLevelUserInstruction: React.FC = () => {

    return (
        <>
            <Alert
                type="warning"
                title="Preencha seus dados"
                message="Para ter acesso a todas as funcionalidades do sistema, preencha suas informações na aba de Perfil"
            />
            <Flex gap={48}>
                <Flex align="center">
                    <Emphasis style={{ textAlign: 'center' }}>
                        Caso queira conhecer melhor o App, acesse as opções do menu inferior e explore as funcionalidades disponíveis.
                    </Emphasis>
                </Flex>
                <Flex align="center" gap={16}>
                    <Emphasis>
                        Para atualizar seu perfil, selecione a seguinte opção
                    </Emphasis>
                    <Link href="/platform/profile/person">
                        <FlatButton type="primary" ghost onPress={() => {}}>
                            Atualizar Perfil
                        </FlatButton>
                    </Link>
                </Flex>
            </Flex>
        </>
    );
}

export default BasicLevelUserInstruction;