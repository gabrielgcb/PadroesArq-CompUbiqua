import styled from "styled-components/native";

const InputLabel = styled.Text`
    font-size: ${({ theme }) => theme.fontSizes.xxxs}px;
    font-family: ${({ theme }) => theme.fonts.regular};
    position: absolute;
    top: 0;
    left: 2px;
`;

export default InputLabel;